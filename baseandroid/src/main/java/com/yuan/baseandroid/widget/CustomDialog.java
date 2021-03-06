package com.yuan.baseandroid.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuan.baseandroid.R;


public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating drawable_circle_title custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private String leftText;
		private String rightText;
		private View contentView;

		private OnClickListener leftClickListener,
				rightClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set drawable_circle_title custom content view for the Dialog. If drawable_circle_title message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param leftText
		 * @param listener
		 * @return
		 */
		public Builder setLeftButton(String leftText, OnClickListener listener) {
			this.leftText = leftText;
			this.leftClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param rightText
		 * @param listener
		 * @return
		 */
		public Builder setRightButton(String rightText, OnClickListener listener) {
			this.rightText = rightText;
			this.rightClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_show, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (leftText != null) {
				((Button) layout.findViewById(R.id.leftButton))
						.setText(leftText);
				if (leftClickListener != null) {
					layout.findViewById(R.id.leftButton)
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									leftClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.leftButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (rightText != null) {
				((Button) layout.findViewById(R.id.rightButton))
						.setText(rightText);
				if (rightClickListener != null) {
					layout.findViewById(R.id.rightButton)
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									rightClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.rightButton).setVisibility(
						View.GONE);
			}

			if (leftText != null && rightText != null) {
				layout.findViewById(R.id.lin_fenge).setVisibility(View.VISIBLE);
			} else {
				layout.findViewById(R.id.lin_fenge).setVisibility(View.GONE);
			}
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(Html.fromHtml(message));
			} else if (contentView != null) {

				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setCancelable(false);
			dialog.setContentView(layout);
			return dialog;
		}
	}
}