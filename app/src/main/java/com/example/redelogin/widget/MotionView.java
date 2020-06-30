package com.example.redelogin.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.example.redelogin.R;
import com.example.redelogin.multitouch.MoveGestureDetector;
import com.example.redelogin.multitouch.RotateGestureDetector;
import com.example.redelogin.widget.entity.MotionEntity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 9/29/16.
 */

public class MotionView  extends FrameLayout {

    private static final String TAG = MotionView.class.getSimpleName();
    boolean drawing = false, erasing = false;
    private float mX, mY, oldX, oldY;
    private int mDotSize;
    private int mColor, eraserColor;
    private ArrayList<Path> mPaths;
    private ArrayList<Paint> mPaints;
    private Path mPath;
    private Paint mPaint;
    Canvas paintCanvas;
    private ArrayList<Boolean> eraserPath;

    public interface Constants {
        float SELECTED_LAYER_ALPHA = 0.15F;
    }

    public interface MotionViewCallback {
        void onEntitySelected(@Nullable MotionEntity entity);
        void onEntityDoubleTap(@NonNull MotionEntity entity);
    }

    // layers
    private final List<MotionEntity> entities = new ArrayList<>();
    @Nullable
    private MotionEntity selectedEntity;

    private Paint selectedLayerPaint;

    // callback
    @Nullable
    private MotionViewCallback motionViewCallback;

    // gesture detection
    private ScaleGestureDetector scaleGestureDetector;
    private RotateGestureDetector rotateGestureDetector;
    private MoveGestureDetector moveGestureDetector;
    private GestureDetectorCompat gestureDetectorCompat;

    // constructors
    public MotionView(Context context) {
        super(context);
        init(context);
    }

    public MotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MotionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MotionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        setWillNotDraw(false);

        selectedLayerPaint = new Paint();
        selectedLayerPaint.setAlpha((int) (255 * Constants.SELECTED_LAYER_ALPHA));
        selectedLayerPaint.setAntiAlias(true);

        // init listeners
        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.rotateGestureDetector = new RotateGestureDetector(context, new RotateListener());
        this.moveGestureDetector = new MoveGestureDetector(context, new MoveListener());
        this.gestureDetectorCompat = new GestureDetectorCompat(context, new TapsListener());

        drawing = false;
        erasing = false;
        setBackgroundColor( Color.WHITE );
        eraserColor = Color.WHITE;
        this.mDotSize = 10;
        this.mColor = Color.BLACK;
        this.mPaths = new ArrayList<>();
        this.eraserPath = new ArrayList<>();
        this.mPaints = new ArrayList<>();
        this.mPath = new Path();
        this.addPath(false);
        this.mX = this.mY = this.oldY = this.oldX = (float) 0.0;

        updateUI();
    }

    private void addPath(boolean fill) {
        mPath = new Path();
        mPaths.add(mPath);
        eraserPath.add( erasing );
        mPaint = new Paint();
        mPaints.add(mPaint);
        mPaint.setColor( mColor );
        if (!fill) mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDotSize);
        mPaint.setStrokeJoin( Paint.Join.ROUND );
        mPaint.setAntiAlias( true );
    }

    public int getDotSize() { return mDotSize; }

    public void setColor() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle(R.string.select_color)
                .initialColor(mColor)
                .wheelType( ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(8) // magic number
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> mColor = selectedColor )
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                } )
                .build()
                .show();
    }

    public void changeDotSize(int increment){
        this.mDotSize += increment;
        int min_DOT_SIZE = 5;
        this.mDotSize = Math.max(mDotSize, min_DOT_SIZE );
        int MAX_DOT_SIZE = 100;
        this.mDotSize = Math.min(mDotSize, MAX_DOT_SIZE );
    }

    public MotionEntity getSelectedEntity() {
        return selectedEntity;
    }

    public List<MotionEntity> getEntities() {
        return entities;
    }

    public void setMotionViewCallback(@Nullable MotionViewCallback callback) {
        this.motionViewCallback = callback;
    }

    public void addEntity(@Nullable MotionEntity entity) {
        if (entity != null) {
            entities.add(entity);
            selectEntity(entity, false);
        }
    }

    public void addEntityAndPosition(@Nullable MotionEntity entity) {
        if (entity != null) {
            initEntityBorder(entity);
            initialTranslateAndScale(entity);
            entities.add(entity);
            selectEntity(entity, true);
            drawing = false;
            erasing = false;
        }
    }

    private void initEntityBorder(@NonNull MotionEntity entity ) {
        // init stroke
        int strokeSize = getResources().getDimensionPixelSize(R.dimen.stroke_size);
        Paint borderPaint = new Paint();
        borderPaint.setStrokeWidth(strokeSize);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor( ContextCompat.getColor(getContext(), R.color.stroke_color));

        entity.setBorderPaint(borderPaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // dispatch draw is called after child views is drawn.
        // the idea that is we draw background stickers, than child views (if any), and than selected item
        // to draw on top of child views - do it in dispatchDraw(Canvas)
        // to draw below that - do it in onDraw(Canvas)
        if (selectedEntity != null) {
            selectedEntity.draw(canvas, selectedLayerPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.paintCanvas = canvas;

        for (int i = 0; i < mPaths.size(); ++i) {
            canvas.drawPath( mPaths.get( i ), mPaints.get( i ) );
        }

        drawAllEntities(canvas);
        super.onDraw(canvas);
    }

    public void clearCanvas() {
        drawing = false;
        erasing = false;
        deletedSelectedEntity();
        entities.clear();
        //eraserPath.clear();
        init(getContext());
    }

    public void drawClicked() {
        drawing = true;
        erasing = false;
    }
    public void eraserClicked() {
        erasing = true;
        drawing = false;
    }

    public void changeBackground() {
        this.setBackgroundColor( mColor );
        eraserColor = mColor;
        drawing = false;
        erasing = false;
        for (int i = 0; i < eraserPath.size(); i++)
        {
            if (eraserPath.get(i)) mPaints.get( i ).setColor(eraserColor);
        }
        updateUI();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mX = event.getX();
        mY = event.getY();
        if (drawing || erasing) {
            if (erasing) {
                this.mPaint.setStrokeCap( Paint.Cap.ROUND );
                this.mPaint.setColor( eraserColor );
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (drawing) {
                        this.addPath( true );
                        this.mPath.addCircle( mX, mY, (float) mDotSize / 2, Path.Direction.CW );
                    }
                    this.addPath( false );
                    this.mPath.moveTo( mX, mY );
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.mPath.lineTo( mX, mY );
                    break;
                case MotionEvent.ACTION_UP:
                    this.addPath( true );
                    if (oldX == mX && oldY == mY && drawing)
                        this.mPath.addCircle( mX, mY, (float) mDotSize / 2, Path.Direction.CW );
                    break;
            }
            updateUI();
            oldX = mX;
            oldY = mY;
        } else if (scaleGestureDetector != null) {
            scaleGestureDetector.onTouchEvent(event);
            rotateGestureDetector.onTouchEvent(event);
            moveGestureDetector.onTouchEvent(event);
            gestureDetectorCompat.onTouchEvent(event);
        }
        return true;
    }

    /**
     * draws all entities on the canvas
     * @param canvas Canvas where to draw all entities
     */
    private void drawAllEntities(Canvas canvas) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).draw(canvas, null);
        }
    }

    /**
     * as a side effect - the method deselects Entity (if any selected)
     * @return bitmap with all the Entities at their current positions
     */
    public Bitmap getThumbnailImage() {
        selectEntity(null, false);

        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // IMPORTANT: always create white background, cos if the image is saved in JPEG format,
        // which doesn't have transparent pixels, the background will be black
        bmp.eraseColor( Color.WHITE);
        Canvas canvas = new Canvas(bmp);
        drawAllEntities(canvas);

        return bmp;
    }

    public void saveImage(){
        Bitmap bitmap = getThumbnailImage();
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        File arquivo = null;
        arquivo = new File( String.valueOf( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
        if (!arquivo.exists()){
            arquivo.mkdirs();
        }
        File file = new File( arquivo, "logo_" + System.currentTimeMillis() + ".png" );
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            Toast.makeText(this.getContext(), String.format("Imagem salva em %s", arquivo.getName()), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        invalidate();
    }

    private void handleTranslate(PointF delta) {
        if (selectedEntity != null) {
            float newCenterX = selectedEntity.absoluteCenterX() + delta.x;
            float newCenterY = selectedEntity.absoluteCenterY() + delta.y;
            // limit entity center to screen bounds
            boolean needUpdateUI = false;
            if (newCenterX >= 0 && newCenterX <= getWidth()) {
                selectedEntity.getLayer().postTranslate(delta.x / getWidth(), 0.0F);
                needUpdateUI = true;
            }
            if (newCenterY >= 0 && newCenterY <= getHeight()) {
                selectedEntity.getLayer().postTranslate(0.0F, delta.y / getHeight());
                needUpdateUI = true;
            }
            if (needUpdateUI) {
                updateUI();
            }
        }
    }

    private void initialTranslateAndScale(@NonNull MotionEntity entity) {
        entity.moveToCanvasCenter();
        entity.getLayer().setScale(entity.getLayer().initialScale());
    }

    private void selectEntity(@Nullable MotionEntity entity, boolean updateCallback) {
        if (selectedEntity != null) {
            selectedEntity.setIsSelected(false);
        }
        if (entity != null) {
            entity.setIsSelected(true);
        }
        selectedEntity = entity;
        invalidate();
        if (updateCallback && motionViewCallback != null) {
            motionViewCallback.onEntitySelected(entity);
        }
        drawing = false;
        erasing = false;
    }

    public void unselectEntity() {
        if (selectedEntity != null) {
            selectEntity(null, true);
        }
    }

    @Nullable
    private MotionEntity findEntityAtPoint(float x, float y) {
        MotionEntity selected = null;
        PointF p = new PointF(x, y);
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).pointInLayerRect(p)) {
                selected = entities.get(i);
                break;
            }
        }
        return selected;
    }

    private void updateSelectionOnTap(MotionEvent e) {
        MotionEntity entity = findEntityAtPoint(e.getX(), e.getY());
        selectEntity(entity, true);
    }

    private void updateOnLongPress(MotionEvent e) {
        // if layer is currently selected and point inside layer - move it to front
        if (selectedEntity != null) {
            PointF p = new PointF(e.getX(), e.getY());
            if (selectedEntity.pointInLayerRect(p)) {
                bringLayerToFront(selectedEntity);
            }
        }
    }

    private void bringLayerToFront(@NonNull MotionEntity entity) {
        // removing and adding brings layer to front
        if (entities.remove(entity)) {
            entities.add(entity);
            invalidate();
        }
    }

    private void moveEntityToBack(@Nullable MotionEntity entity) {
        if (entity == null) {
            return;
        }
        if (entities.remove(entity)) {
            entities.add(0, entity);
            invalidate();
        }
    }

    public void flipSelectedEntity() {
        if (selectedEntity == null) {
            return;
        }
        selectedEntity.getLayer().flip();
        invalidate();
    }

    public void moveSelectedBack() {
        moveEntityToBack(selectedEntity);
    }

    public void deletedSelectedEntity() {
        if (selectedEntity == null) {
            return;
        }
        if (entities.remove(selectedEntity)) {
            selectedEntity.release();
            selectedEntity = null;
            invalidate();
        }
    }

    // memory
    public void release() {
        for (MotionEntity entity : entities) {
            entity.release();
        }
    }

    // gesture detectors

    private class TapsListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (motionViewCallback != null && selectedEntity != null) {
                motionViewCallback.onEntityDoubleTap(selectedEntity);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            updateOnLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            updateSelectionOnTap(e);
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (selectedEntity != null) {
                float scaleFactorDiff = detector.getScaleFactor();
                selectedEntity.getLayer().postScale(scaleFactorDiff - 1.0F);
                updateUI();
            }
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            if (selectedEntity != null) {
                selectedEntity.getLayer().postRotate(-detector.getRotationDegreesDelta());
                updateUI();
            }
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            handleTranslate(detector.getFocusDelta());
            return true;
        }
    }
}
