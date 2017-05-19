package ab.umimagemarklib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AB051788 on 2017/5/2.
 */
public class ImageMarkActivity extends AppCompatActivity {
	protected ImageMark imageMark;
	protected ImageView imageview;

	protected ImageView btnrect;
	protected ImageView btnarrow;
	protected ImageView btntext;
	protected ImageView btncancel;
	protected LinearLayout layoutshape;
	protected LinearLayout layoutcolor;
	protected View line;
	protected TextView save;
	protected List<SelectBean> currentselect;
	protected int[] colorRes = {R.drawable.color_4a90e2, R.drawable.color_50e3c2, R.drawable.color_9013fe, R.drawable.color_b8e986, R.drawable.color_d0021b,R.drawable.color_f5a623,R.drawable.color_f8e71c};
	protected int[] colorValue = {0xFF4a90e2, 0xFF50e3c2, 0xFF9013fe, 0xFFb8e986, 0xFFd0021b,0xFFf5a623,0xFFf8e71c};
	protected int currentColor = 0xFF4a90e2;


	protected ImageView colorIMG;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_mark);
		initView();
	}

	private void initView() {
		btnrect = (ImageView) findViewById(R.id.btnrect);
		btnarrow = (ImageView) findViewById(R.id.btnarrow);
		btntext = (ImageView) findViewById(R.id.btntext);
		btncancel = (ImageView) findViewById(R.id.btncancel);
		imageMark = (ImageMark) findViewById(R.id.imagemark);
		imageview = (ImageView) findViewById(R.id.imagebg);
		layoutshape = (LinearLayout) findViewById(R.id.layoutshape);
		layoutcolor = (LinearLayout) findViewById(R.id.layoutcolor);
		save = (TextView) findViewById(R.id.save);
		line = findViewById(R.id.line);
		btnrect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentselect = getRectSelect();
				showlayoutshape(currentselect);
			}
		});
		btnarrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentselect = getArrowSelect();
				showlayoutshape(currentselect);
			}
		});
		btntext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentselect = getTextSelect();
				showlayoutshape(currentselect);
			}
		});
		btncancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageMark.deleteMark();
			}
		});
//		save.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				File file = MarkUtils.getNewFile(ImageMarkActivity.this, "mark");
//				if (file != null) {
//					MarkBean markBean = imageMark.save(file);
//					Log.e("bean",markBean.toString());
//					Toast.makeText(ImageMarkActivity.this, "saved in " + file.getAbsolutePath(),
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(ImageMarkActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
		imageMark.setImageview(imageview);
//		ImageView imageView = new ImageView(ImageMarkActivity.this);
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120, 120);
//		imageView.setLayoutParams(layoutParams);
//
//		imageView.setImageResource(R.drawable.icon);
//		layoutshape.addView(imageView);
	}


	public void showlayoutshape(List<SelectBean> selects) {
		currentColor = colorValue[0];
		int visible = layoutshape.getVisibility();
		if (visible == View.VISIBLE) {
			layoutshape.setVisibility(View.GONE);
			layoutcolor.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			layoutshape.setVisibility(View.VISIBLE);
		}
		layoutshape.removeAllViews();
		for (int i = 0, size = selects.size(); i < size; i++) {
			SelectBean select = selects.get(i);
			ImageView imageView = (ImageView) LayoutInflater.from(ImageMarkActivity.this).inflate(R.layout.layout_shappebtn, null);
			imageView.setImageResource(select.getIconsrc());
			imageView.setTag(select);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SelectBean select = (SelectBean) v.getTag();
					if(select.getTag() == null){
						showlayoutColor(select);
					}else{
						select.setColor(currentColor);
						imageMark.addMark(select.getMark());
						layoutshape.setVisibility(View.GONE);
						layoutcolor.setVisibility(View.GONE);
						line.setVisibility(View.GONE);
					}
				}
			});
			if(i == 0){
				colorIMG = imageView;
			}
			layoutshape.addView(imageView);
		}
	}


	public void showlayoutColor(final SelectBean select) {
		int visible = layoutcolor.getVisibility();
		if (visible == View.VISIBLE) {
			layoutcolor.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			line.setVisibility(View.VISIBLE);
			layoutcolor.setVisibility(View.VISIBLE);
		}
		layoutcolor.removeAllViews();

		for (int i = 0, size = colorValue.length; i < size; i++) {
			final int j = i;
			ImageView imageView = (ImageView) LayoutInflater.from(ImageMarkActivity.this).inflate(R.layout.layout_colorbtn, null);
			imageView.setImageResource(colorRes[i]);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentColor = colorValue[j];
					SelectBean selectBeen = currentselect.get(0);
					selectBeen.setIconsrc(colorRes[j]);
					if(colorIMG!= null){
						colorIMG.setImageResource(colorRes[j]);
					}
					select.setColor(currentColor);
					layoutcolor.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
				}
			});
			layoutcolor.addView(imageView);
		}
	}

	protected List<SelectBean> selectRec;

	public List<SelectBean> getRectSelect() {
		if (selectRec == null) {
			selectRec = new ArrayList<>();
			SelectBean select1 = new SelectBean(R.drawable.color_4a90e2, null);
			SelectBean select2 = new SelectBean(R.drawable.mark_rect, Mark.Shape.RECT);
			SelectBean select3 = new SelectBean(R.drawable.mark_roundrect, Mark.Shape.ROUNDRECT);
			SelectBean select4 = new SelectBean(R.drawable.mark_oval, Mark.Shape.OVAL);
			SelectBean select5 = new SelectBean(R.drawable.mark_line, Mark.Shape.LINE);
			selectRec.add(select1);
			selectRec.add(select2);
			selectRec.add(select3);
			selectRec.add(select4);
			selectRec.add(select5);

		}
		return selectRec;
	}

	protected List<SelectBean> selectArrow;

	protected List<SelectBean> getArrowSelect() {
		if (selectArrow == null) {
			selectArrow = new ArrayList<>();
			SelectBean select6 = new SelectBean(R.drawable.color_4a90e2, null);
			SelectBean select7 = new SelectBean(R.drawable.mark_arrow, Mark.Shape.ARROW);
			SelectBean select8 = new SelectBean(R.drawable.mark_doubleqrrow, Mark.Shape.BOTHARROW);
			selectArrow.add(select6);
			selectArrow.add(select7);
			selectArrow.add(select8);
		}
		return selectArrow;
	}

	protected List<SelectBean> selectText;

	protected List<SelectBean> getTextSelect() {
		if (selectText == null) {
			selectText = new ArrayList<>();
			SelectBean select9 = new SelectBean(R.drawable.color_4a90e2, null);
			SelectBean select10 = new SelectBean(R.drawable.mark_text, Mark.Shape.TEXT);
			selectText.add(select9);
			selectText.add(select10);
		}
		return selectText;
	}
}
