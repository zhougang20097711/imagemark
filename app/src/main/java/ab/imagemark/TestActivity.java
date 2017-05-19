package ab.imagemark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ab.umimagemarklib.bean.MarkBean;

/**
 * Created by AB051788 on 2017/5/17.
 */
public class TestActivity extends AppCompatActivity {
	private Button btn_new;
	private Button btn_modify;
	private TextView msg;
	private MarkBean markBean;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		btn_new = (Button) findViewById(R.id.btn_new);
		btn_modify = (Button) findViewById(R.id.btn_modify);
		msg = (TextView) findViewById(R.id.msg);
		Intent intent = getIntent();
		markBean = (MarkBean) intent.getSerializableExtra("mark");
		if (markBean != null) {
//			Log.d("bean",markBean.toString());
			msg.setText(markBean.toString());
		}
		btn_new.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		btn_modify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				if (markBean != null) {
					bundle.putSerializable("mark", markBean);
				}
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
}
