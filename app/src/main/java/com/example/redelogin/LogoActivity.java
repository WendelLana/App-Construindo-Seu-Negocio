package com.example.redelogin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.redelogin.ui.TextEditorDialogFragment;
import com.example.redelogin.ui.adapter.FontsAdapter;
import com.example.redelogin.utils.FontProvider;
import com.example.redelogin.viewmodel.Font;
import com.example.redelogin.viewmodel.TextLayer;
import com.example.redelogin.widget.MotionView;
import com.example.redelogin.widget.entity.MotionEntity;
import com.example.redelogin.widget.entity.TextEntity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LogoActivity extends AppCompatActivity implements TextEditorDialogFragment.OnTextLayerCallback {
    private static final int DOT_SIZE_INCREMENT = 5;
    protected MotionView motionView;

    private TextView dotSize;
    private LogoActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private final MotionView.MotionViewCallback motionViewCallback = new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {

        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };

    private FontProvider fontProvider;

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    protected void addTextSticker() {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();

        startTextEntityEditing();
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(TextLayer.Limits.INITIAL_FONT_COLOR);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        if (BuildConfig.DEBUG) {
            textLayer.setText("Texto Padrão");
        }

        return textLayer;
    }

    @Override
    public void textChanged(@Nonnull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }

    private void increaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().increaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void decreaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().decreaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void changeTextEntityColor() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity == null) {
            return;
        }

        int initialColor = textEntity.getLayer().getFont().getColor();

        ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType( ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(8) // magic number
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    TextEntity textEntity1 = currentTextEntity();
                    if (textEntity1 != null) {
                        textEntity1.getLayer().getFont().setColor(selectedColor);
                        textEntity1.updateEntity();
                        motionView.invalidate();
                    }
                } )
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                } )
                .build()
                .show();
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, (dialogInterface, which) -> {
                    TextEntity textEntity = currentTextEntity();
                    if (textEntity != null) {
                        textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                        textEntity.updateEntity();
                        motionView.invalidate();
                    }
                } )
                .show();
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(textEntity.getLayer().getText());
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mark_logo );

        this.fontProvider = new FontProvider(getResources());

        motionView = findViewById(R.id.main_motion_view);
        motionView.setMotionViewCallback(motionViewCallback);

        mSectionsPagerAdapter = new LogoActivity.SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        showPhoneStatePermission();
        
    }

    public static class Tab1 extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState){
            return inflater.inflate(R.layout.tab1_logo, container, false);
        }
    }

    public static class Tab2 extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState){
            return inflater.inflate(R.layout.tab2_logo, container, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Tab1();
                case 1:
                    return new Tab2();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void increaseText(View v) { increaseTextEntitySize(); }
    public void decreaseText(View v) { decreaseTextEntitySize(); }
    public void changeTextColor(View v) { changeTextEntityColor(); }
    public void changeTextFont(View v) { changeTextEntityFont(); }
    public void editText(View v) { startTextEntityEditing(); }

    public void drawClicked(View v){
        motionView.drawClicked();
    }
    public void eraserClicked(View v) {
        motionView.eraserClicked();
    }
    public void clearCanvas(View v){
        motionView.clearCanvas();
    }
    public void changeBackground(View v) { motionView.changeBackground(); }
    public void changeColor(View v) { motionView.setColor();}
    public void saveCanvas(View v) { motionView.saveImage(); }
    public void createText(View v) { addTextSticker(); }
    public void deleteText(View v) { motionView.deletedSelectedEntity(); }
    public void increaseDot(View v) {
        dotSize = findViewById(R.id.sizeDot);
        motionView.changeDotSize(DOT_SIZE_INCREMENT);
        String texto = String.valueOf(motionView.getDotSize());
        dotSize.setText( texto );
    }
    public void decreaseDot(View v) {
        dotSize = findViewById(R.id.sizeDot);
        motionView.changeDotSize(-DOT_SIZE_INCREMENT);
        String texto = String.valueOf(motionView.getDotSize());
        dotSize.setText( texto );
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation();
            } else {
                requestPermission();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @Nonnull String[] permissions,
            @Nonnull int[] grantResults) {
        int REQUEST_PERMISSION_STORAGE = 1;
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText( this, "Permission Garantida!", Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( this, "Permission Recusada!", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( "Permissão necessária!" )
                .setMessage( "Gravar imagem logo_round no celular." )
                .setPositiveButton(android.R.string.ok, (dialog, id) -> requestPermission() );
        builder.create().show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
