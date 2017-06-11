package jp.ac.titech.itpro.sdl.calculator;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_ACC = "MainActivity.acc";
    private static final String KEY_NUM = "MainActivity.num";
    private static final String KEY_OP = "MainActivity.op";
    private static final String KEY_STATE = "MainActivity.state";

    private static final int MAX_LENGTH = 10;

    private static final int OP_ADD = 0;
    private static final int OP_SUB = 1;
    private static final int OP_MUL = 2;
    private static final int OP_DIV = 3;

    private static final int STATE_OP = 0;
    private static final int STATE_NUM = 1;
    private static final int STATE_DOT = 2;

    private TextView outputView;
    private int[] numButtonIds;
    private BigDecimal acc = BigDecimal.ZERO;
    private BigDecimal num = BigDecimal.ZERO;
    private int op = OP_ADD;
    private int state = STATE_OP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        String[] numButtons = res.getStringArray(R.array.num_buttons);
        numButtonIds  = new int[numButtons.length];
        for (int i = 0; i < numButtons.length; i++) {
            numButtonIds[i] = res.getIdentifier(numButtons[i], "id", getPackageName());
        }

        outputView = (TextView) findViewById(R.id.output_view);

        for (int i = 0; i < numButtonIds.length; i++) {
            findViewById(numButtonIds[i]).setOnClickListener(this);
        }

        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_sub).setOnClickListener(this);
        findViewById(R.id.button_mul).setOnClickListener(this);
        findViewById(R.id.button_div).setOnClickListener(this);
        findViewById(R.id.button_eq).setOnClickListener(this);
        findViewById(R.id.button_ac).setOnClickListener(this);
        findViewById(R.id.button_c).setOnClickListener(this);
        findViewById(R.id.button_pc).setOnClickListener(this);
        findViewById(R.id.button_neg).setOnClickListener(this);
        findViewById(R.id.button_dot).setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ACC, acc.toString());
        outState.putString(KEY_NUM, num.toString());
        outState.putInt(KEY_OP, op);
        outState.putInt(KEY_STATE, state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        acc = new BigDecimal(savedInstanceState.getString(KEY_ACC));
        num = new BigDecimal(savedInstanceState.getString(KEY_NUM));
        op = savedInstanceState.getInt(KEY_OP);
        state = savedInstanceState.getInt(KEY_STATE);

        switch (state) {
            case STATE_OP:
                outputView.setText(acc.toString());
                break;

            case STATE_NUM:
                outputView.setText(num.toString());
                break;

            case STATE_DOT:
                outputView.setText(num.toString() + ".");
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String str = num.toString();
        for (int i = 0; i < numButtonIds.length; i++) {
            if (id == numButtonIds[i]) {
                switch (state) {
                    case STATE_OP:
                        num = new BigDecimal(i);
                        break;

                    case STATE_NUM:
                        if (str.length() < MAX_LENGTH) {
                            num = new BigDecimal(num.toString() + String.valueOf(i));
                        }
                        break;

                    case STATE_DOT:
                        if (str.length() < MAX_LENGTH) {
                            num = new BigDecimal(num.toString() + "." + String.valueOf(i));
                        }
                        break;

                    default:
                        break;
                }
                state = STATE_NUM;
                outputView.setText(num.toString());
                return;
            }
        }

        switch (id) {
            case R.id.button_add:
                if (state == STATE_NUM || state == STATE_DOT) {
                    calculate();
                }
                op = OP_ADD;
                break;

            case R.id.button_sub:
                if (state == STATE_NUM || state == STATE_DOT) {
                    calculate();
                }
                op = OP_SUB;
                break;

            case R.id.button_mul:
                if (state == STATE_NUM || state == STATE_DOT) {
                    calculate();
                }
                op = OP_MUL;
                break;

            case R.id.button_div:
                if (state == STATE_NUM || state == STATE_DOT) {
                    calculate();
                }
                op = OP_DIV;
                break;

            case R.id.button_eq:
                calculate();
                break;

            case R.id.button_ac:
                num = BigDecimal.ZERO;
                acc = BigDecimal.ZERO;
                op = OP_ADD;
                state = STATE_OP;
                outputView.setText("0");
                break;

            case R.id.button_c:
                num = BigDecimal.ZERO;
                state = STATE_NUM;
                outputView.setText("0");
                break;

            case R.id.button_pc:
                num = num.divide(new BigDecimal(100));
                calculate();
                outputView.setText(num.toString());
                break;

            case R.id.button_neg:
                switch (state) {
                    case STATE_OP:
                        acc = acc.negate();
                        outputView.setText(acc.toString());
                        break;

                    case STATE_NUM:
                        num = num.negate();
                        outputView.setText(num.toString());
                        break;

                    case STATE_DOT:
                        num = num.negate();
                        state = STATE_NUM;
                        outputView.setText(num.toString());
                        break;

                    default:
                        break;
                }
                break;

            case R.id.button_dot:
                switch (state) {
                    case STATE_OP:
                        num = BigDecimal.ZERO;
                        outputView.setText("0.");
                        state = STATE_DOT;
                        break;

                    case STATE_NUM:
                        if (!str.contains(".")) {
                            outputView.append(".");
                            state = STATE_DOT;
                        }
                        break;

                    case STATE_DOT:
                        break;

                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    private void calculate() {
        switch (op) {
            case OP_ADD:
                acc = acc.add(num);
                break;

            case OP_SUB:
                acc = acc.subtract(num);
                break;

            case OP_MUL:
                acc = acc.multiply(num);
                break;

            case OP_DIV:
                if (num.compareTo(BigDecimal.ZERO) == 0) {
                    acc = BigDecimal.ZERO;
                } else {
                    acc = acc.divide(num, MAX_LENGTH, BigDecimal.ROUND_HALF_UP);
                }
                break;

            default:
                break;
        }

        state = STATE_OP;
        outputView.setText(acc.toPlainString());
    }
}
