package com.github.arekolek.phone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_call.*
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data.schemeSpecificPart
    }

    override fun onStart() {
        super.onStart()

        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }

        disposables.addAll(
                OngoingCall.state.subscribe(::updateUi),
                OngoingCall.state
                        .filter { it == Call.STATE_DISCONNECTED }
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe {
                            finish()
                        }
        )
    }

    private fun updateUi(state: Int) {
        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }
}
