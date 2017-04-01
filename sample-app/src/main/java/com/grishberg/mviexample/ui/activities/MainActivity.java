package com.grishberg.mviexample.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grishberg.mviexample.R;
import com.grishberg.mviexample.mvp.presenters.FirstScreenPresenter;
import com.grishberg.mviexample.mvp.state.first.FirstPresenterStateModel.RequestState;
import com.grishberg.mviexample.mvp.state.first.FirstViewStateModel.ErrorState;
import com.grishberg.mviexample.mvp.state.first.FirstViewStateModel.ProgressState;
import com.grishberg.mviexample.mvp.state.first.FirstViewStateModel.SuccessState;
import com.grishberg.mviexample.ui.view.BalanceView;
import com.grishberg.mvpstatelibrary.framework.state.MvpState;
import com.grishberg.mvpstatelibrary.framework.ui.BaseMvpActivity;

import java.util.Locale;

/**
 * FirstScreen View
 */
public class MainActivity extends BaseMvpActivity<FirstScreenPresenter>
        implements View.OnClickListener {
    private ProgressBar progressBar;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView countTextView;
    private Button buttonStart;
    private Button buttonSecondStep;
    private Button buttonThirdStep;
    private BalanceView balanceView; // view of nested presenter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initWidgets();
        initButtonHandlers();
        //register view of nested presenter to parent life cycle
        balanceView.registerNestedView(this, savedInstanceState);
    }

    private void initWidgets() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        countTextView = (TextView) findViewById(R.id.countTextView);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonSecondStep = (Button) findViewById(R.id.buttonSecondStep);
        buttonThirdStep = (Button) findViewById(R.id.buttonThirdStep);
        balanceView = (BalanceView) findViewById(R.id.balanceView);
    }

    private void initButtonHandlers() {
        buttonStart.setOnClickListener(this);
        buttonSecondStep.setOnClickListener(this);
        buttonThirdStep.setOnClickListener(this);
    }

    @Override
    protected FirstScreenPresenter createPresenter() {
        return new FirstScreenPresenter();
    }

    /**
     * Update view after changes from presenter
     *
     * @param state - model with updates from presenter
     */
    @Override
    public void onModelUpdated(final MvpState state) {
        if (state instanceof SuccessState) {
            updateValues((SuccessState) state);
        } else if (state instanceof ErrorState) {
            showError();
        } else if (state instanceof ProgressState) {
            showProgress((ProgressState) state);
        }
    }

    private void showProgress(ProgressState state) {
        progressBar.setVisibility(state.isProgress() ? View.VISIBLE : View.GONE);
        if (state.isProgress()) {
            buttonStart.setEnabled(false);
            buttonSecondStep.setEnabled(false);
        }
    }

    private void updateValues(SuccessState state) {
        titleTextView.setText(state.getTitle());
        descriptionTextView.setText(state.getDescription());
        countTextView.setText(String.format(Locale.US, "%d", state.getCount()));
        buttonSecondStep.setEnabled(true);
    }

    private void showError() {
        buttonStart.setEnabled(true);
        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonStart:
                getPresenter().updateState(new RequestState());
                break;

            case R.id.buttonSecondStep:
                SecondActivity.start(this);
                break;

            default:
                ThirdActivity.start(this);
                break;
        }
    }
}