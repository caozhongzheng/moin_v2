package com.moinapp.wuliao.modules.feedback;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.listener.UserFeedbackListener;
import com.moinapp.wuliao.utils.CommonMethod;
import com.moinapp.wuliao.utils.ToastUtils;

public class FeedBackActivity extends BaseActivity {

	private EditText mContent;
	private EditText mContact;
	private Button btnSubmit;
	private boolean mShowConfirm;
	private static final int MAX_LENGTH = 600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_layout);

		initTopBar(getString(R.string.feedback_bad));
		initLeftBtn(true, R.drawable.back_gray);
		initRightBtn(false, "");

		mContent = (EditText) findViewById(R.id.feedback_content);
		mContact = (EditText) findViewById(R.id.feedback_contact);

		mContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				MAX_LENGTH) {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (source.length() > 0 && dest.length() == MAX_LENGTH) {
                    Toast.makeText(FeedBackActivity.this, R.string.over_length_limit,
                            Toast.LENGTH_SHORT).show();
				}
				return super.filter(source, start, end, dest, dstart, dend);
			}
		} });

		btnSubmit = (Button) findViewById(R.id.submit);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mContent.getText().toString().trim();
				String contact = mContact.getText().toString().trim();

				if (TextUtils.isEmpty(content)) {
                    Toast.makeText(FeedBackActivity.this, R.string.feedback_empty,
                            Toast.LENGTH_SHORT).show();
					return;
				}

				if (!CommonMethod.hasActiveNetwork(FeedBackActivity.this)) {
                    Toast.makeText(FeedBackActivity.this, R.string.no_network,
                            Toast.LENGTH_SHORT).show();
					return;
				}
				MineManager.getInstance().userFeedback(content, contact, new UserFeedbackListener() {
					@Override
					public void userFeedbackSucc(int result) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Context mContext = FeedBackActivity.this;
								ToastUtils.toast(mContext, R.string.feedback_succ);
									btnSubmit.setEnabled(false);
									finish();
							}
						});
					}

					@Override
					public void onNoNetwork() {
						ToastUtils.toast(FeedBackActivity.this, R.string.no_network);
					}

					@Override
					public void onErr(Object object) {
					}
				});
			}
		});

	}

	@Override
	public void onBackPressed() {
		String content = mContent.getText().toString().trim();
		if (!TextUtils.isEmpty(content) && !mShowConfirm) {
			ToastUtils.toast(FeedBackActivity.this, R.string.feedback_confirm);
			mShowConfirm = true;
		} else {
			finish();
		}
	}

}
