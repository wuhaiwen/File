package com.whw.file;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CHOICE_MODE = 1;
    private long exitime = 0;
    private ActionBar actionBar;

    private ListView listView;
    ImageView imageView;
    //数据
    ArrayList<File> data;
    //适配器
    FileAdapter fileAdapter;

    boolean isRoot;
    int counter;
    String title;
    File currentFile;
    CheckBox checkbox;
    Sort sort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        imageView = (ImageView) findViewById(R.id.iv_nothing);
        initVIew();

    }

    private void initVIew() {
        sort = new Sort();
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        listView = (ListView) findViewById(R.id.listView);
        data = new ArrayList<>();
        title = "/storage";
        //加载sd卡中的文件
        File sdPath = Environment.getExternalStorageDirectory();
        showList(sdPath, title);
        currentFile = sdPath;
        fileAdapter = new FileAdapter(this, data);
        listView.setAdapter(fileAdapter);
        listView.setMultiChoiceModeListener(new MumListener());
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setOnItemClickListener(new LicListener());
    }

    //绑定数据
    public void showList(File file, String title) {
        currentFile = file;
        actionBar.setTitle(title);

        //每次调用前清除上一级中的数据
        data.clear();
        //过滤掉以"."开头的文件或文件
        File[] files = file.listFiles(new MyFilter());
        Arrays.sort(files, sort);
        for (File f : files) {
            data.add(f);
        }

        //观察者模式，适配器有改变则刷新页面
        if (fileAdapter != null) {
            fileAdapter.notifyDataSetChanged();
        }
        //判断是否为根目录
        isRoot = title.equals("/storage") ? true : false;
    }

    //listView多选监听器
    class MumListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int count = listView.getCheckedItemCount();

            //checkbox.setChecked(true);
            mode.setTitle(count + "");
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.button_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


    class LicListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File file = data.get(position);
            if (file.isDirectory()) {
                title += "/" + file.getName();
                showList(file, title);
                if (file.listFiles().length == 0) {
                    //如果为空文件，则把为空的图片的显示
                    imageView.setVisibility(View.VISIBLE);
                }
            } else {
                openFile(file);
            }
        }

        private void openFile(File file) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            //获得文件类型
            MimeTypeMap map = MimeTypeMap.getSingleton();
            Uri data = Uri.fromFile(file);
            String name = file.getName();
            String typeName = name.substring(name.lastIndexOf(".") + 1);
            String type = map.getMimeTypeFromExtension(typeName);

            if (type != null) {
                //用隐式意图打开文件
                intent.setDataAndType(data, type);
                startActivity(intent);
            }
        }

    }

    //文件过滤器，过滤掉以"."开头的文件
    class MyFilter implements FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.getName().startsWith("."))
                return false;
            else
                return true;
        }
    }


    @Override
    public void onBackPressed() {
        imageView.setVisibility(View.INVISIBLE);
        //checkbox.setVisibility(View.INVISIBLE);
        if (isRoot) {
            counter++;
            //根目录前提下按两次退出；
            if (System.currentTimeMillis() - exitime > 2000) {
                showToast("再按一次退出程序");
                exitime = System.currentTimeMillis();
                return;
            } else {
                finish();
                System.exit(0);
            }
        } else {
            title = (String) actionBar.getTitle()
                    .subSequence(0, (actionBar.getTitle()
                            .length() - currentFile.getName().length() - 1));
            //counter = 0;
        }
        showList(currentFile.getParentFile(), title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        //获得菜单中的某一项
        MenuItem item = menu.findItem(R.id.action_search);
        //搜索输入框
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("请输入文件名....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showToast(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_add:
                createNew();
                //dfFileUtil.createFolder(currentFile,file_name,currentFile.listFiles());
                break;
            case R.id.action_refresh:
                // if (title.equals("isRoot"))
                showList(currentFile, title);

        }
        chooseSortModle(id);
        return super.onOptionsItemSelected(item);
    }

    private void createNew() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_create, null);
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("创建新的文件夹")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = editText.getText().toString();
                        if (fileName.length() > 0) {
                            FileUtil.createFolder(currentFile, fileName, data);
                            showToast("创建成功");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    private void showToast(String str) {

        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void chooseSortModle(int id) {
        switch (id) {
            case R.id.sort_by_folder:
                sort = new Sort();
                sort.setAction("folder");
                showToast(sort.getAction());
                break;
            case R.id.sort_by_time:
                sort = new Sort();
                sort.setAction("lastModify");
                showToast(sort.getAction());
                break;
            case R.id.sort_by_size_asc:
                sort = new Sort();
                sort.setAction("asc");
                showToast(sort.getAction());
                break;
            case R.id.sort_by_size_desc:
                sort = new Sort();
                sort.setAction("desc");
                showToast(sort.getAction());
                break;
            case R.id.sort_by_abc:
                sort = new Sort();
                sort.setAction("name");
                showToast(sort.getAction());
                break;
        }
        showList(currentFile, title);
    }


}
