package com.example.uiappliction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uiappliction.activity.HomeFragment;
import com.example.uiappliction.activity.PersonFragment;
import com.example.uiappliction.activity.ZhongFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView txt_home;
    private ImageView txt_book;
    private ImageView txt_me;
    private FrameLayout ly_content;
    private HomeFragment homeFragment;
    private ZhongFragment bookListFragment;
    private PersonFragment personFragment;
    private FragmentManager fManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }
    private void bindViews() {
        txt_home = findViewById(R.id.txt_home);
        txt_book =findViewById(R.id.txt_book);
        ly_content = findViewById(R.id.ly_content);
        txt_me = findViewById(R.id.txt_me);

        txt_home.setOnClickListener(this);
        txt_book.setOnClickListener(this);
        txt_me.setOnClickListener(this);

        fManager = getSupportFragmentManager();
        fragmentTransaction = fManager.beginTransaction();
        if(homeFragment==null){
            homeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.ly_content,homeFragment);
        }
        fragmentTransaction.show(homeFragment);
        txt_home.setImageResource(R.mipmap.home);
        fragmentTransaction.commit();
    }

    //设置文本的选中状态
    private void setSelected(){
        txt_home.setSelected(false);
        txt_book.setSelected(false);
        txt_me.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction transaction){
        if(homeFragment!=null) transaction.hide(homeFragment);
        if(bookListFragment!=null) transaction.hide(bookListFragment);
        if(personFragment!=null) transaction.hide(personFragment);
    }

    @Override
    public void onClick(View view) {

        fragmentTransaction = fManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (view.getId()){
            case R.id.txt_home:
                setSelected();
                txt_home.setSelected(true);
                if(homeFragment==null){
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.ly_content,homeFragment);
                }
                else{
                    fragmentTransaction.show(homeFragment);
                }
                txt_home.setImageResource(R.mipmap.home);
                txt_book.setImageResource(R.mipmap.no_zhong);
                txt_me.setImageResource(R.mipmap.noprofile);
                break;
            case  R.id.txt_book:
                setSelected();
                txt_book.setSelected(true);
                if(bookListFragment==null){
                    bookListFragment = new ZhongFragment();
                    fragmentTransaction.add(R.id.ly_content,bookListFragment);
                }
                else{
                    fragmentTransaction.show(bookListFragment);
                }
                txt_home.setImageResource(R.mipmap.home1);
                txt_book.setImageResource(R.mipmap.zhong);
                txt_me.setImageResource(R.mipmap.noprofile);
                break;
            case  R.id.txt_me:
                setSelected();
                txt_me.setSelected(true);
                if(personFragment==null){
                    personFragment = new PersonFragment();
                    fragmentTransaction.add(R.id.ly_content,personFragment);
                }
                else{
                    fragmentTransaction.show(personFragment);
                }
                txt_home.setImageResource(R.mipmap.home1);
                txt_book.setImageResource(R.mipmap.no_zhong);
                txt_me.setImageResource(R.mipmap.profile);
                break;
        }
        fragmentTransaction.commit();

    }
}
