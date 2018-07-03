package io.paysky.ui.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.paybutton.R;

import java.util.LinkedList;


public class CardEditText extends AppCompatEditText implements TextWatcher {

    private static final char SPACE_CHAR = ' ';
    private static final String SPACE_STRING = String.valueOf(SPACE_CHAR);
    private static final int GROUPSIZE = 4;
    private boolean isUpdating = false;
    private String type;
    private ImageView cardTypeImage;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public CardEditText(Context context) {
        super(context);
        addTextWatcher();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public CardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextWatcher();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public CardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextWatcher();
    }


    public void setCardTypeImage(ImageView cardTypeImage) {
        this.cardTypeImage = cardTypeImage;
        changeIcon();
    }


    private void addTextWatcher() {
        addTextChangedListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void changeIcon() {
        String s = getText().toString().replace(" ", "").trim();
        int cardIcon;
        if (s.startsWith("4") || s.matches(CardPattern.VISA)) {
            cardIcon = R.drawable.vi;
            type = "Visa";
        } else if (s.matches(CardPattern.MASTERCARD_SHORTER) || s.matches(CardPattern.MASTERCARD_SHORT) || s.matches(CardPattern.MASTERCARD)) {
            cardIcon = R.drawable.mc;
            type = "MasterCard";
        } else if (s.matches(CardPattern.AMERICAN_EXPRESS)) {
            cardIcon = R.drawable.am;
            type = "American_Express";
        } else if (s.matches(CardPattern.DISCOVER_SHORT) || s.matches(CardPattern.DISCOVER)) {
            cardIcon = R.drawable.ds;
            type = "Discover";
        } else if (s.matches(CardPattern.JCB_SHORT) || s.matches(CardPattern.JCB)) {
            cardIcon = R.drawable.jcb;
            type = "JCB";
        } else if (s.matches(CardPattern.DINERS_CLUB_SHORT) || s.matches(CardPattern.DINERS_CLUB)) {
            cardIcon = R.drawable.dc;
            type = "Diners_Club";
        } else {
            cardIcon = R.drawable.card_icon;
            type = "UNKNOWN";
        }

        if (cardTypeImage!=null){
            cardTypeImage.setImageResource(cardIcon);
        }

    }

    public String getCardNumber() {
        return getText().toString().replace(" ", "").trim();
    }

    public boolean isValid() {
        if (getCardNumber().matches(CardPattern.VISA_VALID)) return true;
        if (getCardNumber().matches(CardPattern.MASTERCARD_VALID)) return true;
        if (getCardNumber().matches(CardPattern.AMERICAN_EXPRESS_VALID)) return true;
        if (getCardNumber().matches(CardPattern.DISCOVER_VALID)) return true;
        if (getCardNumber().matches(CardPattern.DINERS_CLUB_VALID)) return true;
        if (getCardNumber().matches(CardPattern.JCB_VALID)) return true;
        return false;
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        changeIcon();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String originalString = s.toString();

        // Check if we are already updating, to avoid infinite loop.
        // Also check if the string is already in a valid format.
        String regexp = "^(\\d{4}\\s)*\\d{0,4}(?<!\\s)$";
        if (isUpdating || originalString.matches(regexp)) {
            return;
        }

        // Set flag to indicate that we are updating the Editable.
        isUpdating = true;

        // First all whitespaces must be removed. Find the index of all whitespace.
        LinkedList<Integer> spaceIndices = new LinkedList<>();
        for (int index = originalString.indexOf(SPACE_CHAR); index >= 0; index = originalString.indexOf(SPACE_CHAR, index + 1)) {
            spaceIndices.offerLast(index);
        }

        // Delete the whitespace, starting from the end of the string and working towards the beginning.
        Integer spaceIndex;
        while (!spaceIndices.isEmpty()) {
            spaceIndex = spaceIndices.removeLast();
            s.delete(spaceIndex, spaceIndex + 1);
        }

        // Loop through the string again and add whitespaces in the correct positions
        for (int i = 0; ((i + 1) * GROUPSIZE + i) < s.length(); i++) {
            s.insert((i + 1) * GROUPSIZE + i, SPACE_STRING);
        }

        // Finally check that the cursor is not placed before a whitespace.
        // This will happen if, for example, the user deleted the digit '5' in
        // the string: "1234 567".
        // If it is, move it back one step; otherwise it will be impossible to delete
        // further numbers.
        int cursorPos = getSelectionStart();
        if (cursorPos > 0 && s.charAt(cursorPos - 1) == SPACE_CHAR) {
            setSelection(cursorPos - 1);
        }

        isUpdating = false;

    }
}
