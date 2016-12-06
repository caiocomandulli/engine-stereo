package com.comandulli.engine.panoramic.playback.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.R.color;
import android.R.id;
import android.R.layout;
import android.R.style;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileChooserDialog {
	private String m_sdcardDirectory = "";
	private final Context m_context;
	private TextView m_titleView;

	private String m_dir = "";
	private List<String> m_subdirectories;
	private final ChosenFileListener m_chosenDirectoryListener;
	private ArrayAdapter<String> m_listAdapter;

	private boolean isChoosingMode;
	private int m_mode;

	private String parentDirectory = "";

	// ////////////////////////////////////////////////////
	// Callback interface for selected directory
	// ////////////////////////////////////////////////////
	public interface ChosenFileListener {
		void onChosenDir(String chosen, int mode, String parent);
	}

	public FileChooserDialog(Context context, ChosenFileListener chosenDirectoryListener) {
		m_context = context;
		m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		m_chosenDirectoryListener = chosenDirectoryListener;

		try {
			m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
		} catch (IOException ignored) {
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// chooseDirectory() - load directory chooser dialog for initial
	// default sdcard directory
	// /////////////////////////////////////////////////////////////////////

	public void chooseDirectory() {
		// Initial directory is sdcard directory
		chooseDirectory(m_sdcardDirectory);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// chooseDirectory(String dir) - load directory chooser dialog for initial
	// input 'dir' directory
	// //////////////////////////////////////////////////////////////////////////////

	public void chooseDirectory(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dir = m_sdcardDirectory;
		}

		try {
			dir = new File(dir).getCanonicalPath();
		} catch (IOException ioe) {
			return;
		}

		m_dir = dir;
		m_subdirectories = getDirectories(dir);

		class DirectoryOnClickListener implements OnClickListener {
			@Override
            public void onClick(DialogInterface dialog, int which) {
				if (isChoosingMode) {
					m_mode = which;
					m_chosenDirectoryListener.onChosenDir(m_dir, m_mode, parentDirectory);
					dialog.dismiss();
				} else {
					m_dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
					File file = new File(m_dir);
					if (file.isFile()) {
						parentDirectory = file.getParent();
						chooseMode();
					} else {
						// Navigate into the sub-directory
						updateDirectory();
					}
				}
			}
		}

		Builder dialogBuilder = createDirectoryChooserDialog(dir, m_subdirectories, new DirectoryOnClickListener());
		final AlertDialog dirsDialog = dialogBuilder.create();

		dirsDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					// Back button pressed
					if (m_dir.equals(m_sdcardDirectory)) {
						// The very top level directory, do nothing
						return false;
					} else {
						// Navigate back to an upper directory
						m_dir = new File(m_dir).getParent();
						updateDirectory();
					}

					return true;
				} else {
					return false;
				}
			}
		});

		// Show directory chooser dialog
		dirsDialog.show();
	}

	private List<String> getDirectories(String dir) {
		List<String> dirs = new ArrayList<>();

		try {
			File dirFile = new File(dir);
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return dirs;
			}

			for (File file : dirFile.listFiles()) {
				dirs.add(file.getName());
			}
		} catch (Exception ignored) {
		}

		Collections.sort(dirs, new Comparator<String>() {
			@Override
            public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});

		return dirs;
	}

	private Builder createDirectoryChooserDialog(String title, List<String> listItems, OnClickListener onClickListener) {
		Builder dialogBuilder = new Builder(m_context);

		// Create custom view for AlertDialog title containing
		// current directory TextView and possible 'New folder' button.
		// Current directory TextView allows long directory path to be wrapped
		// to multiple lines.
		LinearLayout titleLayout = new LinearLayout(m_context);
		titleLayout.setOrientation(LinearLayout.VERTICAL);

		m_titleView = new TextView(m_context);
		m_titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		m_titleView.setTextAppearance(m_context, style.TextAppearance_Large);
		m_titleView.setTextColor(m_context.getResources().getColor(color.white));
		m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		m_titleView.setText(title);

		titleLayout.addView(m_titleView);

		dialogBuilder.setCustomTitle(titleLayout);

		m_listAdapter = createListAdapter(listItems);

		dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
		dialogBuilder.setCancelable(false);

		return dialogBuilder;
	}

	private void updateDirectory() {
		m_subdirectories.clear();
		m_subdirectories.addAll(getDirectories(m_dir));
		m_titleView.setText(m_dir);

		m_listAdapter.notifyDataSetChanged();
		isChoosingMode = false;
	}

	private void chooseMode() {
		m_subdirectories.clear();
		List<String> modeList = new ArrayList<>();
		modeList.add("Flat");
		modeList.add("Over/Under");
		modeList.add("Left/Right");
		m_subdirectories.addAll(modeList);
		m_titleView.setText(m_dir);
		m_listAdapter.notifyDataSetChanged();
		isChoosingMode = true;
	}

	private ArrayAdapter<String> createListAdapter(List<String> items) {
		return new ArrayAdapter<String>(m_context, layout.select_dialog_item, id.text1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				if (v instanceof TextView) {
					// Enable list item (directory) text wrapping
					TextView tv = (TextView) v;
					tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
					tv.setEllipsize(null);
				}
				return v;
			}
		};
	}
}