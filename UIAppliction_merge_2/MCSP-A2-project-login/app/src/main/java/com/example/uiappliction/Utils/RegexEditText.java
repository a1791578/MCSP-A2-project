package com.example.uiappliction.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.uiappliction.R;

import java.util.regex.Pattern;


public class RegexEditText extends AppCompatEditText implements InputFilter {

    
    public static final String REGEX_MOBILE = "[1]\\d{0,10}";
    
    public static final String REGEX_CHINESE = "[\\u4e00-\\u9fa5]*";
    
    public static final String REGEX_ENGLISH = "[a-zA-Z]*";
    
    public static final String REGEX_NUMBER = "\\d*";
    
    public static final String REGEX_COUNT = "[1-9]\\d*";
    
    public static final String REGEX_NAME = "[[\\u4e00-\\u9fa5]|[a-zA-Z]|\\d]*";
    
    public static final String REGEX_NONNULL = "\\S+";

    
    private Pattern mPattern;

    public RegexEditText(Context context) {
        this(context, null);
    }

    public RegexEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public RegexEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegexEditText);

        if (array.hasValue(R.styleable.RegexEditText_inputRegex)) {
            setInputRegex(array.getString(R.styleable.RegexEditText_inputRegex));
        } else if (array.hasValue(R.styleable.RegexEditText_regexType)) {
            int regexType = array.getInt(R.styleable.RegexEditText_regexType, 0);
            switch (regexType) {
                case 0x01:
                    setInputRegex(REGEX_MOBILE);
                    break;
                case 0x02:
                    setInputRegex(REGEX_CHINESE);
                    break;
                case 0x03:
                    setInputRegex(REGEX_ENGLISH);
                    break;
                case 0x04:
                    setInputRegex(REGEX_NUMBER);
                    break;
                case 0x05:
                    setInputRegex(REGEX_COUNT);
                    break;
                case 0x06:
                    setInputRegex(REGEX_NAME);
                    break;
                case 0x07:
                    setInputRegex(REGEX_NONNULL);
                    break;
                default:
                    break;
            }
        }

        array.recycle();
    }

    
    public boolean hasInputType(int type) {
        return (getInputType() & type) != 0;
    }

    
    public void addInputType(int type) {
        setInputType(getInputType() | type);
    }

    
    public void removeInputType(int type) {
        setInputType(getInputType() & ~type);
    }

    
    public void setInputRegex(String regex) {
        if (TextUtils.isEmpty(regex)) {
            return;
        }

        mPattern = Pattern.compile(regex);
        addFilters(this);
    }

    
    public String getInputRegex() {
        if (mPattern == null) {
            return null;
        }
        return mPattern.pattern();
    }

    
    public void addFilters(InputFilter filter) {
        if (filter == null) {
            return;
        }

        final InputFilter[] newFilters;
        final InputFilter[] oldFilters = getFilters();
        if (oldFilters != null && oldFilters.length > 0) {
            newFilters = new InputFilter[oldFilters.length + 1];
            // 复制旧数组的元素到新数组中
            System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.length);
            newFilters[oldFilters.length] = filter;
        } else {
            newFilters = new InputFilter[1];
            newFilters[0] = filter;
        }
        super.setFilters(newFilters);
    }

    
    public void clearFilters() {
        super.setFilters(new InputFilter[0]);
    }

    
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int destStart, int destEnd) {
        if (mPattern == null) {
            return source;
        }

        // 拼接出最终的字符串
        String begin = dest.toString().substring(0, destStart);
        String over = dest.toString().substring(destStart + (destEnd - destStart), destStart + (dest.toString().length() - begin.length()));
        String result = begin + source + over;

        // 判断是插入还是删除
        if (destStart > destEnd - 1) {
            // 如果是插入字符
            if (!mPattern.matcher(result).matches()) {
                // 如果不匹配就不让这个字符输入
                return "";
            }
        } else {
            // 如果是删除字符
            if (!mPattern.matcher(result).matches()) {
                // 如果不匹配则不让删除（删空操作除外）
                if (!"".equals(result)) {
                    return dest.toString().substring(destStart, destEnd);
                }
            }
        }

        // 不做任何修改
        return source;
    }
}