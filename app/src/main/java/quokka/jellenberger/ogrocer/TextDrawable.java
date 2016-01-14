package quokka.jellenberger.ogrocer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by jellenberger on 12/31/15.
 */
public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
    private Context mContext;

    public TextDrawable(String text) {
        this.text = text;

        this.paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(6f, 0, 0, Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }
    public TextDrawable(String text, Context context, int tabID) {
        this.text = text;

        this.paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.primaryText));
        paint.setTextSize(context.getResources().getDimension(R.dimen.text_size_large));
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        //paint.setShadowLayer(6f, 0, 0, Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        if (tabID == 0)
            paint.setTextAlign(Paint.Align.RIGHT); //these seem backward but kind of work?
        else
            paint.setTextAlign(Paint.Align.LEFT);
    }

    public TextDrawable(Context context, int tabID) {

        this.paint = new Paint();
        mContext = context;
        paint.setColor(context.getResources().getColor(R.color.primaryColor));
        paint.setTextSize(context.getResources().getDimension(R.dimen.text_size_large));
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        //paint.setShadowLayer(6f, 0, 0, Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        if (tabID == 0) {
            paint.setTextAlign(Paint.Align.LEFT);
            this.text = "Save for Later";
        }
        else {
            paint.setTextAlign(Paint.Align.RIGHT);
            this.text = "Add to Cart";
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(mContext.getResources().getColor(R.color.grey100));
        //canvas.drawColor(0xDEffffff);
        if (paint.getTextAlign() == Paint.Align.LEFT)
            canvas.drawText(text, 32.0f , canvas.getHeight() / 2.0f + 18.0f, paint);
        else
            canvas.drawText(text, canvas.getWidth() - 32.0f , canvas.getHeight() / 2.0f + 18.0f, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}