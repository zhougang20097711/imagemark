package ab.imagemark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import ab.umimagemarklib.ImageMarkActivity;
import ab.umimagemarklib.MarkUtils;
import ab.umimagemarklib.bean.MarkBean;

public class MainActivity extends ImageMarkActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageview.setImageResource(R.mipmap.bg);

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File file = MarkUtils.getNewFile(MainActivity.this, "mark");
				if (file != null) {
					MarkBean markBean = imageMark.save(file);
					Log.e("bean",markBean.toString());
					Intent intent = new Intent(MainActivity.this,TestActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("mark",markBean);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
//					Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
//							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
				}
			}
		});

		resume = false;
	}

private boolean resume = false;
	@Override
	protected void onResume() {
		super.onResume();
		if(!resume){
			Intent intent = getIntent();
			MarkBean markBean = (MarkBean) intent.getSerializableExtra("mark");
			if(markBean != null){
				imageMark.modifyMarkBean(markBean);
			}
			resume = true;
		}

	}
}
