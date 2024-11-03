package com.example.uiappliction.action;

import com.example.uiappliction.R;


public interface AnimAction {


    int ANIM_DEFAULT = -1;


    int ANIM_EMPTY = 0;


    int ANIM_SCALE = R.style.ScaleAnimStyle;


    int ANIM_IOS = R.style.IOSAnimStyle;


    int ANIM_TOAST = android.R.style.Animation_Toast;


    int ANIM_TOP = R.style.TopAnimStyle;


    int ANIM_BOTTOM = R.style.BottomAnimStyle;


    int ANIM_LEFT = R.style.LeftAnimStyle;


    int ANIM_RIGHT = R.style.RightAnimStyle;
}