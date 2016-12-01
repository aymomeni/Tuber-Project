package cs4000.tuber;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Ali on 12/1/2016.
 */

public class RectClassView extends View {
  public RectClassView(Context context) {
	super(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);

	Rect ourRect = new Rect();
	ourRect.set(-200, -222, canvas.getWidth()/2, canvas.getWidth()/5);

	Paint blue = new Paint();
	blue.setColor(Color.BLUE);
	blue.setStyle(Paint.Style.FILL);

	canvas.drawRect(ourRect, blue);

  }


}
