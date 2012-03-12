package com.github.his;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FonflatterPoCActivity extends Activity {

	private String titleText = "";

	private ImageView imgView;

	private Bitmap bitmap;

	String[] getUrlAndTitletext() throws IOException {

		String[] result = new String[2];

		Document doc = Jsoup.connect("http://www.fonflatter.de").get();

		Element imga = doc.select("div#page div#content a[href~=http://www.fonflatter.de/.*png]").first();
		result[0] = imga.attr("href");

		Element titletext = doc.select("div#page div#content img[title~=#\\d{4,}.*]").first();
		result[1] = titletext.attr("title");

		return result;

	}

	private void loadImage() {
		final Handler handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(final Message msg) {
				if (msg.what == 0) {
					imgView.setImageBitmap(bitmap);
					Toast.makeText(getBaseContext(), "Geladen", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "Es tut mir leid, der Comic konnte nicht geladen werden.",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
		new Thread(new Runnable() {
			public void run() {
				try {
					String[] urlAndTitle = getUrlAndTitletext();
					titleText = urlAndTitle[1];
					bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlAndTitle[0]).getContent());
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		imgView = (ImageView) findViewById(R.id.fonflatterimage);
		imgView.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				Toast.makeText(v.getContext(), titleText, Toast.LENGTH_LONG).show();
			}
		});
		loadImage();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add("Neu laden");
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(final MenuItem item) {
				loadImage();
				return true;
			}
		});
		return true;
	}
}
