package hit.treasure.activity;

import java.util.Stack;

import com.guoyang.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalculatorActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);
		
		// ��ʼ����Ӧstack��stringBuilder
		numStack = new Stack<Double>();
		operateStack = new Stack<String>();		
		lastNumBuilder = new StringBuilder();
		commandBuilder = new StringBuilder();
		
		initView();		
	}
	
	/**
	 * ��ʼ�������Ȱ�ť���Լ���Ӱ�ť�����¼�
	 */
	private void initView() {
		inputEditText = (EditText)findViewById(R.id.input);
		inputEditText.setKeyListener(null);
		inputEditText.setText("0");
		
		resultEditText = (EditText)findViewById(R.id.result);
		resultEditText.setKeyListener(null);
		resultEditText.setText("0");
		
		numButton = new Button[10];
		operatorButton = new Button[4];
		numID = new int[]{R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
				R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9};
		operatorID = new int[]{R.id.add, R.id.sub, R.id.multiply, R.id.divide};
		
		numButton[0] = (Button)findViewById(R.id.num0);
		numButton[1] = (Button)findViewById(R.id.num1);
		numButton[2] = (Button)findViewById(R.id.num2);
		numButton[3] = (Button)findViewById(R.id.num3);
		numButton[4] = (Button)findViewById(R.id.num4);
		numButton[5] = (Button)findViewById(R.id.num5);
		numButton[6] = (Button)findViewById(R.id.num6);
		numButton[7] = (Button)findViewById(R.id.num7);
		numButton[8] = (Button)findViewById(R.id.num8);
		numButton[9] = (Button)findViewById(R.id.num9);
		
		// ��Ӱ��������¼�
		for (Button button : numButton) {
			button.setOnClickListener(this);
		}
		
		operatorButton[0] = (Button)findViewById(R.id.add);
		operatorButton[1] = (Button)findViewById(R.id.sub);
		operatorButton[2] = (Button)findViewById(R.id.multiply);
		operatorButton[3] = (Button)findViewById(R.id.divide);
		
		// ��Ӱ��������¼�
		for (Button button : operatorButton) {
			button.setOnClickListener(this);
		}
		
		point = (Button)findViewById(R.id.point);
		equal = (Button)findViewById(R.id.equal);
		backspace = (Button)findViewById(R.id.backspace);
		clear = (Button)findViewById(R.id.clear);
		
		point.setOnClickListener(this);
		equal.setOnClickListener(this);
		backspace.setOnClickListener(this);
		clear.setOnClickListener(this);
		
		Toast.makeText(CalculatorActivity.this, add(10, 12) + "", Toast.LENGTH_LONG)
		.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewID = v.getId();
		
		// ���Ϊ���ֵĻ�����ӵ���ǰ��������stringBuilder�У���������ʾ
		for (int i = 0; i < numID.length; i++) {
			if (viewID == numID[i]) {
				lastNumBuilder.append(i);
				commandBuilder.append(i);
				inputEditText.setText(commandBuilder.toString());
				return;
			}
		}
		
		// �����������Ļ�������ǰ��������stringBuilder����ȡ������
		// ��ӵ�������ջ�У�ͬʱ�������ͬʱѹջ
		for (int i = 0; i < operatorID.length; i++) {
			if (viewID == operatorID[i]) {	
				if (lastNumBuilder.length() > 0) {
					try {
						Double operand = Double.parseDouble(lastNumBuilder.toString());
						numStack.push(operand);
						operateStack.push(OPERATORS[i]);
						commandBuilder.append(OPERATORS[i]);
						inputEditText.setText(commandBuilder);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						lastNumBuilder.delete(0, lastNumBuilder.length());
					}
				}				
				return;
			}
		}
		
		// ���Ϊ"."�Ļ������жϵ�ǰ���������Ƿ���"."���������
		if (viewID == R.id.point) {
			if (!lastNumBuilder.toString().contains(".")) {
				lastNumBuilder.append(".");
				commandBuilder.append(".");
				inputEditText.setText(commandBuilder);
			}
		}
		
		// ���˼���Ӧ�¼�
		if (viewID == R.id.backspace) {
			if (commandBuilder.length() > 0) {
				char c = commandBuilder.charAt(commandBuilder.length() - 1);
				if (isOperator(Character.toString(c))) {
					// ��Ϊ�������Ļ�����������ջ���������˻ص�stringBuilder��
					operateStack.pop();
					lastNumBuilder.append(numStack.pop());
				}
				else if ((c > '0' && c < '9') || c == '.') {
					// ����һ���ַ�Ϊ���ֻ���"."�Ļ������޸�builder����
					lastNumBuilder.deleteCharAt(lastNumBuilder.length() - 1);
				} else {}
				commandBuilder.deleteCharAt(commandBuilder.length() - 1);
				inputEditText.setText(commandBuilder);
			}
		}
		
		// �����ť�������Ӧ��ջ��builder
		if (viewID == R.id.clear) {
			clearOperate();
			inputEditText.setText("0");
			resultEditText.setText("0");
		}
		
		if (viewID == R.id.equal) {
			if (lastNumBuilder.length() > 0) {
				try {
					// �����һ��������ѹ��ջ
					numStack.push(Double.parseDouble(lastNumBuilder.toString()));
					
					//����������ʾ
					double result = calculate(numStack, operateStack);
					resultEditText.setText(Double.toString(result));
					clearOperate();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void clearOperate() {
		numStack.clear();
		operateStack.clear();
		lastNumBuilder.delete(0, lastNumBuilder.length());
		commandBuilder.delete(0, commandBuilder.length());
	}
	
	private double calculate(Stack<Double> nStack, Stack<String> opeStack) {
		// ����������������󣬵������һ��������
		if (opeStack.empty() && nStack.size() == 1) {
			return nStack.pop();
		}
		double numOne = nStack.pop();
		String opeStr = opeStack.peek();
		if (opeStr.equals("*") || opeStr.equals("/")) {
			// �������ĳ˳���ͳһ������
			numOne = calculator(nStack, opeStack, numOne);
		}
		if (!opeStack.empty()) {
			opeStr = opeStack.pop();
			if (opeStr.equals("+")) {
				return calculate(nStack, opeStack) + numOne;
			}
			return calculate(nStack, opeStack) - numOne;
		}
		else 
		{
			return numOne;
		}
	}
	
	// ��Ҫ���ڼ���˳�������������˳�����Ľ��
	private double calculator(Stack<Double> nStack, Stack<String> opeStack, double num) {
		if (opeStack.empty()) {
			return num;
		}
		String opeStr = opeStack.pop();
		if (opeStr.equals("+") || opeStr.equals("-")) {
			opeStack.push(opeStr);
			return num;
		}
		double numTwo = nStack.pop();
		if (opeStr.equals("*")) {
			return calculator(nStack, opeStack, numTwo) * num;		
		}
		return calculator(nStack, opeStack, numTwo) / num;		
	}
	
	// �����ж��Ƿ��������
	private boolean isOperator(String opeStr) {
		for (String str : OPERATORS) {
			if (str.equals(opeStr)) {
				return true;
			}
		}
		return false;
	}
	
	public native double add(double num1, double num2);
	public native double sub(double num1, double num2);
	public native double mul(double num1, double num2);
	public native double div(double num1, double num2);
	
	static {
        System.loadLibrary("calculator");
    }
	
	private static final String[] OPERATORS = {"+", "-", "*", "/"};
	private StringBuilder lastNumBuilder;		// ��ǰ������
	private StringBuilder commandBuilder;		// ��ǰ��������
	private Stack<Double> numStack;				// ������ջ
	private Stack<String> operateStack;			// ������ջ
	private EditText inputEditText;				// �༭��
	private EditText resultEditText;
	private Button[] numButton;					// ���ְ�ť
	private Button[] operatorButton;			// ��������ť
	private int[] numID;						// ���ְ�����Ӧ����ԴID	
	private int[] operatorID;					// ����������Ӧ����ԴID
	private Button backspace;					// ���˼�
	private Button clear;						// �����ť
	private Button point;						// С���㰴ť
	private Button equal;						// �����ť
}
