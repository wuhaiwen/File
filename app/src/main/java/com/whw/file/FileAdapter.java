package com.whw.file;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wuhaiwen on 2016/4/16.
 */
public class FileAdapter extends BaseAdapter {

    private ArrayList<File> files;

    private Context context;

    private LayoutInflater layoutInflater;

    CoordinatorLayout layout;

    EditText editText;

    /**
     * 创建文件适配器
     *
     * @param context
     * @param files
     */
    public FileAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    /**
     * 获得特定位置的数据项
     *
     * @param position
     * @return
     */
    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    //获得特定位置的数据编号（数据库读取数据时用到）
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 获得特定位置的视图项
     *
     * @param position
     * @param convertView View 可重用的视图项
     * @param parent      视图组（这里来说就是listview）
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ImageButtonListener listener;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.file_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        listener = new ImageButtonListener();
        listener.setPositon(position);
        File file = files.get(position);
        viewHolder.bindData(file);
        viewHolder.imageButton.setOnClickListener(listener);
        //Log.d("getView", "holder: " + viewHolder.id + ", position: " + position);
        return convertView;
    }

    //int i = 0;
    class ViewHolder {
        ImageView imageView;
        TextView file_name;
        TextView file_size;
        TextView direct_num;
        ImageButton imageButton;
//        CheckBox checkBox;
        // int id;

        //绑定数据，让数据显示在模板上
        public void bindData(File file) {
            if (file.isDirectory()) {
                imageView.setImageResource(R.drawable.folder);
                file_name.setText(file.getName());
                file_size.setText("文件：" + FileUtil.countFile(file));
                //如果是文件夹，设置该textView可见
                direct_num.setVisibility(View.VISIBLE);
                direct_num.setText("文件夹: " + FileUtil.countDir(file));
            } else {
                String fileName = file.getName();
                String _file = null;
                try {
                    _file = fileName.substring(fileName.lastIndexOf("."), fileName.length());
//                    Log.d("文件名", _file);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                try {
                    switch (_file) {
                        case ".txt":
                            imageView.setImageResource(R.drawable.txt);
                            break;
                        case ".zip":
                            imageView.setImageResource(R.drawable.zip);
                            break;
                        case ".mp3":
                            imageView.setImageResource(R.drawable.mp3);
                            break;
                        default:
                            imageView.setImageResource(R.drawable.unkown);
                            break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                //imageView.setImageResource(R.drawable.txt);

                file_name.setText(file.getName());
                file_size.setText(FileUtil.getSize(file.length()));
                //如果不是文件夹，设置该textView不可见
                direct_num.setVisibility(View.INVISIBLE);
            }


        }


        public ViewHolder(View v) {
            //id = i++;
            imageView = (ImageView) v.findViewById(R.id.imageView);
            file_name = (TextView) v.findViewById(R.id.file_name);
            file_size = (TextView) v.findViewById(R.id.file_size);
            direct_num = (TextView) v.findViewById(R.id.direct_num);
            imageButton = (ImageButton) v.findViewById(R.id.imageButton_more);
//            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        }


    }


    public class ImageButtonListener implements ImageButton.OnClickListener {
        private int positon;

        public void setPositon(int positon) {
            this.positon = positon;
        }

        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(context, v);
            menu.inflate(R.menu.button_menu);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_copy:
                            Toast.makeText(context, "选择要粘贴的位置", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_delete:
                            new AlertDialog.Builder(context)
                                    .setTitle("是否真的删除")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FileUtil.deleteFolders(files.get(positon));
                                            //将该位置的文件从data中移除，可以出现刷新的效果
                                            files.remove(positon);
                                            Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    })
                                    .create().show();
                            // doNewOne();
                            notifyDataSetChanged();
                            break;
                        case R.id.action_rename:
                            final View view = layoutInflater.inflate(R.layout.dialog_rename, null);
                            final EditText editText = (EditText) view.findViewById(R.id.editText_rename);
                            //editText.setInputType(InputType.TYPE_CLASS_TEXT);
                            new AlertDialog.Builder(context)
                                    .setTitle("请输入文件名")
                                    .setView(view)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String file_name = editText.getText().toString();
                                            if(file_name.length()>0){
                                                File f =  FileUtil.rename(files.get(positon),file_name);
                                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                                files.set(positon,f);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    })
                                    .create()
                                    .show();
                            break;
                    }
                    return true;
                }
            });
            menu.show();
        }
    }

    private void doNewOne() {
        //执行操作

        //提示
        //视图
        //文本
        //时间长度
        Snackbar.make(layout, "已新建", Snackbar.LENGTH_LONG)
                .setAction("撤销", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        return;
                    }
                }).show();
    }
}
