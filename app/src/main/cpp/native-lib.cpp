#include <jni.h>
#include <opencv2/opencv.hpp>

extern "C"
JNIEXPORT void JNICALL
Java_com_example_healthapp_NativeLib_reliefFilter(
        JNIEnv *env,
        jobject,
        jbyteArray input,
        jbyteArray output,
        jint width,
        jint height) {

    jbyte* in = env->GetByteArrayElements(input, nullptr);
    jbyte* out = env->GetByteArrayElements(output, nullptr);

    cv::Mat imgRGBA(height, width, CV_8UC4, in);

// RGBA → BGR
    cv::Mat img;
    cv::cvtColor(imgRGBA, img, cv::COLOR_RGBA2BGR);

// параметры (можно потом передавать из Java)
    float k = 15.0f;
    float mapK = 15.0f;

// =========================
// RELIEF
// =========================
    cv::Mat gray;
    cv::cvtColor(img, gray, cv::COLOR_BGR2GRAY);

    cv::Mat grayDenoised;
    cv::bilateralFilter(gray, grayDenoised, 7, 30, 30);

    cv::Mat smooth;
    cv::GaussianBlur(grayDenoised, smooth, cv::Size(0, 0), 5.0);

    cv::Mat detail16s;
    cv::subtract(grayDenoised, smooth, detail16s, cv::noArray(), CV_16S);

// карта (можешь потом использовать отдельно)
    cv::Mat absDetail8u;
    cv::convertScaleAbs(detail16s, absDetail8u);

    cv::Mat mapF;
    absDetail8u.convertTo(mapF, CV_32F);
    mapF *= mapK;

    cv::Mat mapGray;
    cv::normalize(mapF, mapGray, 0, 255, cv::NORM_MINMAX);
    mapGray.convertTo(mapGray, CV_8U);

// основной результат
    cv::Mat detailF, baseF;
    detail16s.convertTo(detailF, CV_32F);
    grayDenoised.convertTo(baseF, CV_32F);

    cv::Mat enhancedF = baseF + detailF * k;

    cv::Mat enhanced8u;
    enhancedF.convertTo(enhanced8u, CV_8U);

    // 🔥 CLAHE (улучшает текстуру кожи)
    cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE(2.0, cv::Size(8,8));
    clahe->apply(enhanced8u, enhanced8u);

// обратно в RGBA
    cv::Mat resultRGBA;
    cv::cvtColor(enhanced8u, resultRGBA, cv::COLOR_GRAY2RGBA);

// копируем обратно
    memcpy(out, resultRGBA.data, width * height * 4);

    env->ReleaseByteArrayElements(input, in, 0);
    env->ReleaseByteArrayElements(output, out, 0);
}
