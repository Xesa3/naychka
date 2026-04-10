package com.example.healthapp.Pacients;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;
import com.google.android.material.snackbar.Snackbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.TextView;


public class PatientCardsFragment extends Fragment {
    private SharedViewModel viewModel; // Это у нас источник данных список пациентов
    private RecyclerView recyclerView; // Это список карточек
    private PatientAdapter adapter; // Это как бы мост между передачей данных и отображением

    private Button btnNewPatientCard; // Кнопка создания нового пациента
    private SearchView searchView; // Строка поиска

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_patient_cards, container, false);


        bindViews(view); // Определение элементов

        setupRecycler(); // Настройка списка и адаптера

        setupViewModel(); // создание ViewModel и тестовые данные

        setupObservers(); //подписки на LiveData

        setupSearch(); // Поиск карточек

        setupDragAndDrop(); // Перенос элементов


        btnNewPatientCard.setOnClickListener(v -> {
            // Переход на второй фрагмент
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new CreateNewPatientCard())
                    .addToBackStack(null) // чтобы можно было вернуться назад
                    .commit();
        });


        return view;
    }

    //Задание кнопок
    private void bindViews(View view){
        btnNewPatientCard = view.findViewById(R.id.btnNewPatientCard); // Кнопка создать карточку
        recyclerView = view.findViewById(R.id.PatientListCard); // Список где карточки
        searchView = view.findViewById(R.id.searchView);
    }


    // Настройка RecuclerView(списка) + adapter
    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PatientAdapter(new ArrayList<>(), patient -> openPatientDetails(patient));
        recyclerView.setAdapter(adapter); // Подключение адаптера
    }

    private void openPatientDetails(Patient patient) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("patient", patient);

        PatientDetailsFragment fragment = new PatientDetailsFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    // создание ViewModel и тестовые данные
    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.initTestDataPatient(); // тестовые данные
    }


    private void setupObservers(){
        viewModel.getPatientList().observe(getViewLifecycleOwner(), patients -> {
            adapter.updateList(patients);
        }); // Обновление списка как только он изменился
    }

    private void setupSearch() {
        // Блок поиск карточки в списке
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | 0

        ) {
            @Override
            public boolean isLongPressDragEnabled() {
                return !adapter.isFilterActive();
            }
            // при перемещении говорим адаптеру поменять местаами карточки
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                adapter.moveItem(fromPos, toPos);
                return true;
            }
            // Когда пользователь отпустил элемент сохраняем новый список
            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // сохраняем новый порядок только после отпуска
                recyclerView.post(() -> {
                    viewModel.setPatientList(adapter.getCurrentPatients());
                });
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                List<Patient> currentList = new ArrayList<>(viewModel.getPatientList().getValue());
                Patient deletedPatient = currentList.get(position);

                currentList.remove(position);

                // временно обновляем UI через ViewModel
                viewModel.setPatientList(currentList);

                String textSnackbar = recyclerView.getContext().getString(R.string.delete_patient2);

                Snackbar snackbar = Snackbar.make(recyclerView, textSnackbar, Snackbar.LENGTH_LONG);

                String textSnackbAraction = recyclerView.getContext().getString(R.string.delete_patient3);

                snackbar.setAction(textSnackbAraction, v -> {
                    currentList.add(position, deletedPatient);
                    viewModel.setPatientList(currentList);
                });

// ---------- 🎨 СТИЛИЗАЦИЯ ----------
                View sbView = snackbar.getView();

// фон со скруглением
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.parseColor("#2E2E2E")); // мягкий тёмный
                bg.setCornerRadius(40f);

                sbView.setBackground(bg);

// отступы (чтобы не на весь экран)
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) sbView.getLayoutParams();
                params.setMargins(32, 0, 32, 50);
                sbView.setLayoutParams(params);

// текст
                TextView text = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                text.setTextColor(Color.WHITE);
                text.setTextSize(15);

// кнопка "Отменить"
                TextView action = sbView.findViewById(com.google.android.material.R.id.snackbar_action);
                action.setTextColor(Color.parseColor("#FF8A80")); // мягкий красный
                action.setTextSize(14);

// ---------- логика ----------
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            // уже удалён окончательно
                        }
                    }
                });

                snackbar.show();

            }


            @Override
            public boolean canDropOver(@NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.ViewHolder current,
                                       @NonNull RecyclerView.ViewHolder target) {
                return true;
            }


            @Override
            public void onChildDraw(
                    Canvas c,
                    RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder,
                    float dX,
                    float dY,
                    int actionState,
                    boolean isCurrentlyActive
            ) {
                View itemView = viewHolder.itemView;

                float width = itemView.getWidth();
                float progress = Math.min(1f, Math.abs(dX) / width); // 0 → 1

                Paint paint = new Paint();
                paint.setAntiAlias(true);

                if (dX < 0) {
                    // 🎨 мягкий красный
                    paint.setColor(Color.parseColor("#E57373"));

                    RectF background = new RectF(
                            itemView.getRight() + dX + 20,
                            itemView.getTop() + 12,
                            itemView.getRight() - 12,
                            itemView.getBottom() - 12
                    );

                    // скруглённый фон
                    c.drawRoundRect(background, 40f, 40f, paint);

                    // ---------- ИКОНКА ----------
                    Drawable icon = ContextCompat.getDrawable(
                            recyclerView.getContext(),
                            android.R.drawable.ic_menu_delete
                    );

                    int iconSize = 80;
                    int iconMargin = 40;

                    int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                    int iconBottom = iconTop + iconSize;

                    int iconRight = itemView.getRight() - iconMargin;
                    int iconLeft = iconRight - iconSize;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    // прозрачность = зависит от свайпа
                    icon.setAlpha((int)(255 * progress));
                    icon.draw(c);

                    // ---------- ТЕКСТ ----------
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(42);
                    paint.setAlpha((int)(255 * progress)); // плавное появление

                    String text = recyclerView.getContext().getString(R.string.delete_patient);

                    float textWidth = paint.measureText(text);

                    float textX = iconLeft - textWidth - 30;

                    Paint.FontMetrics fm = paint.getFontMetrics();
                    float textY = itemView.getTop() + itemView.getHeight()/2f - (fm.ascent + fm.descent)/2;

                    c.drawText(text, textX, textY, paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}