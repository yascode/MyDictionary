package xyz.mydictionary;

/*
MyEventSchedule を利用して、フラグメント画面にする
 */

import java.text.DateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

//import de.greenrobot.daoexample.DaoMaster.DevOpenHelper;
import xyz.yascode.mydictionary.DictionaryDaoOpenHelper;

public class MainActivity  extends ListActivity {

	private SQLiteDatabase db;
	private EditText editText1, editText2, editText3, editText4;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private DictionaryDao dictionaryDao;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//初期化？
		DictionaryDaoOpenHelper helper = new DictionaryDaoOpenHelper(this, "dictionary-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		dictionaryDao = daoSession.getDictionaryDao();

		String wordColumn = DictionaryDao.Properties.Word.columnName;
		String orderBy = wordColumn + " COLLATE LOCALIZED ASC";
		cursor = db.query(dictionaryDao.getTablename(), dictionaryDao.getAllColumns(), null, null, null, null, orderBy);
		String[] from = { wordColumn, DictionaryDao.Properties.Description.columnName };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
		setListAdapter(adapter);

		editText1 = (EditText) findViewById(R.id.editTextWord);
		editText2 = (EditText) findViewById(R.id.editTextCategory);
		editText3 = (EditText) findViewById(R.id.editTexDescription);
		editText4 = (EditText) findViewById(R.id.editTextUrl);
		addUiListeners();
	}

	protected void addUiListeners() {
		editText1.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addDictionary();
					return true;
				}
				return false;
			}
		});

		final View button = findViewById(R.id.register_button);
		button.setEnabled(false);
		editText1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean enable = s.length() != 0;
				button.setEnabled(enable);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void onMyButtonClick(View view) {
		addDictionary();
	}

	private void addDictionary() {
		String dictionaryWord = editText1.getText().toString();
		String dictionaryCategory = editText2.getText().toString();
		String dictionaryDescription = editText3.getText().toString();
		String dictionaryUrl = editText4.getText().toString();

		editText1.setText("");
		editText2.setText("");
		editText3.setText("");
		editText4.setText("");

//		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
//		String date = "Added on " + df.format(new Date());
		Dictionary dictionary = new Dictionary(null, dictionaryWord, dictionaryCategory, dictionaryDescription, dictionaryUrl, new Date());
		dictionaryDao.insert(dictionary);
		Log.d("MyDictionary", "Inserted new dictionary, ID: " + dictionary.getId());

		cursor.requery();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		dictionaryDao.deleteByKey(id);
		Log.d("MyDictionary", "Deleted dictionary, ID: " + id);
		cursor.requery();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//
//		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
}
