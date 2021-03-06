package com.natura.android.textfield

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.natura.android.R
import com.natura.android.icon.FontIcon

@SuppressLint("CustomViewStyleable")
class TextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // TODO trocar FontIcon por AppCompatImageView
    enum class State {
        NONE, ERROR, SUCCESS
    }

    enum class LayoutState(val borderWidth: Int, val borderColor: Int, val labelColor: Int, val textColor: Int, val footerColor: Int) {
        DEFAULT(R.dimen.ds_border_tiny, R.color.colorLowEmphasis, R.color.colorMediumEmphasis, R.color.colorMediumEmphasis, R.color.colorMediumEmphasis),
        FILLED(R.dimen.ds_border_tiny, R.color.colorHighEmphasis, R.color.colorMediumEmphasis, R.color.colorHighEmphasis, R.color.colorMediumEmphasis),
        DISABLED(R.dimen.ds_border_tiny, R.color.colorLowEmphasis, R.color.colorLowEmphasis, R.color.colorLowEmphasis, R.color.colorLowEmphasis),
        FOCUSED(R.dimen.ds_border_emphasis, R.color.colorBrdNatYellow, R.color.colorMediumEmphasis, R.color.colorHighEmphasis, R.color.colorMediumEmphasis),
        ERROR(R.dimen.ds_border_emphasis, R.color.colorBrdNatRed, R.color.colorBrdNatRed, R.color.colorHighEmphasis, R.color.colorBrdNatRed),
        SUCCESS(R.dimen.ds_border_tiny, R.color.colorBrdNatGreen, R.color.colorBrdNatGreen, R.color.colorHighEmphasis, R.color.colorBrdNatGreen)
    }

    private val SUCCESS_ICON = "EA15"
    private val ERROR_ICON = "EA13"

    private val inputLabel by lazy { findViewById<TextView>(R.id.text_field_input_label) }

    private val inputBox by lazy { findViewById<LinearLayout>(R.id.text_field_input_box) }
    private val inputValue by lazy { findViewById<EditText>(R.id.text_field_input_value) }
    private val inputIcon by lazy { findViewById<FontIcon>(R.id.text_field_input_icon) }

    private val footerBox by lazy { findViewById<LinearLayout>(R.id.text_field_input_footer_box) }
    private val footerValue by lazy { findViewById<TextView>(R.id.text_field_input_footer) }
    private val footerIcon by lazy { findViewById<FontIcon>(R.id.text_field_input_footer_icon) }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        inputLabel?.isEnabled = enabled
        inputValue?.isEnabled = enabled
        inputIcon?.isEnabled = enabled
        footerIcon?.isEnabled = enabled
        footerValue?.isEnabled = enabled
        resetLayoutState()
    }

    val editTextView: EditText
        get() {
            return inputValue
        }

    var inputType: Int = EditorInfo.TYPE_CLASS_TEXT
        set(value) {
            field = value
            inputValue?.inputType = value
            inputValue?.setSelection(inputValue?.text?.length ?: 0)
        }

    var hint: String? = null
        set(value) {
            field = value
            inputValue.hint = value
        }

    var maxLength: Int = 0
        set(value) {
            field = value

            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(value)
            inputValue.filters = fArray
        }

    var maxLines: Int = 0
        set(value) {
            field = value
            inputValue.maxLines = value
        }

    var lines: Int = 0
        set(value) {
            field = value
            inputValue.setLines(value)
        }

    var text: String? = null
        set(value) {
            field = value
            inputValue.setText(value)
            resetLayoutState()
        }
        get() {
            return inputValue.text.toString()
        }

    var icon: String? = null
        set(value) {
            field = value
            inputIcon.setText(value)
            changeVisibilityByValue(inputIcon, value)
        }

    var label: String? = null
        set(value) {
            field = value
            inputLabel.text = value
            changeVisibilityByValue(inputLabel, value)
        }

    var footer: String? = null
        set(value) {
            field = value
            footerValue.text = value
            changeVisibilityByValue(footerBox, value)
        }

    var layoutState: LayoutState = LayoutState.DEFAULT
        private set(value) {
            field = value
            inputLabel?.setTextColor(ContextCompat.getColor(context, value.labelColor))
            (inputBox.background as GradientDrawable).setStroke(
                resources.getDimension(value.borderWidth).toInt(),
                ContextCompat.getColor(context, value.borderColor))
            footerValue?.setTextColor(ContextCompat.getColor(context, value.footerColor))
            footerIcon?.setTextColor(ContextCompat.getColor(context, value.footerColor))
        }

    var state: State = State.NONE
        set(value) {
            field = value
            resetLayoutState()
            when (value) {
                State.ERROR -> {
                    setFooterIcon(ERROR_ICON, View.VISIBLE)
                }
                State.SUCCESS -> {
                    setFooterIcon(SUCCESS_ICON, View.VISIBLE)
                }
                else -> {
                    setFooterIcon("", View.GONE)
                }
            }
        }

    var error: String? = null
        set(value) {
            field = value
            footer = value
            state = if (value != null) {
                State.ERROR
            } else {
                State.NONE
            }
        }

    private fun resetLayoutState() {
        layoutState = when (state) {
            State.ERROR -> LayoutState.ERROR
            State.SUCCESS -> LayoutState.SUCCESS
            else -> {
                if (!isEnabled) LayoutState.DISABLED
                else if (inputValue.isFocused) LayoutState.FOCUSED
                else if (inputValue.text.isNotEmpty()) LayoutState.FILLED
                else LayoutState.DEFAULT
            }
        }
    }

    private fun setFooterIcon(value: String, visibility: Int) {
        footerIcon.text = value
        footerIcon.visibility = visibility
    }

    private fun changeVisibilityByValue(view: View, value: String?) {
        if (value == null || value.isEmpty()) view.visibility = View.GONE
        else view.visibility = View.VISIBLE
    }

    private fun intToState(vstate: Int) = when (vstate) {
            1 -> State.SUCCESS
            2 -> State.ERROR
            else -> State.NONE
        }

    init {
        View.inflate(context, R.layout.ds_text_field_input, this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ds_text_field_input)

        val vinputType = typedArray.getInteger(R.styleable.ds_text_field_input_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        val vhint = typedArray.getString(R.styleable.ds_text_field_input_android_hint)
        val vmaxLength = typedArray.getInteger(R.styleable.ds_text_field_input_android_maxLength, Integer.MAX_VALUE)
        val vmaxLines = typedArray.getInteger(R.styleable.ds_text_field_input_android_maxLines, 1)
        val vlines = typedArray.getInteger(R.styleable.ds_text_field_input_android_lines, 1)
        val venabled = typedArray.getBoolean(R.styleable.ds_text_field_input_android_enabled, true)

        val vlabel = typedArray.getString(R.styleable.ds_text_field_input_text_field_label)
        val vtext = typedArray.getString(R.styleable.ds_text_field_input_text_field_text)
        val vicon = typedArray.getString(R.styleable.ds_text_field_input_text_field_icon)
        val vfooter = typedArray.getString(R.styleable.ds_text_field_input_text_field_footer)
        val vstate = typedArray.getInt(R.styleable.ds_text_field_input_text_field_state, 0)

        typedArray.recycle()

        inputType = vinputType
        hint = vhint
        maxLines = vmaxLines
        maxLength = vmaxLength
        lines = vlines
        isEnabled = venabled

        text = vtext
        label = vlabel
        footer = vfooter
        icon = vicon
        state = intToState(vstate)

        inputValue.setOnFocusChangeListener { _, hasFocus -> onFocusChanged(hasFocus) }
        inputIcon.setOnClickListener { _ -> onFocusChanged(true) }

        inputBox.setOnClickListener {
            inputValue.requestFocus()
        }
    }

    private fun onFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            layoutState = LayoutState.FOCUSED
        } else {
            resetLayoutState()
        }
    }

    fun setOnIconClickListener(l: OnClickListener?) {
        inputIcon?.setOnClickListener(l)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            childrenStates = saveChildViewStates()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                state.childrenStates?.let { restoreChildViewStates(it) }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    internal class SavedState : BaseSavedState {

        internal var childrenStates: SparseArray<Parcelable>? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            childrenStates = source.readSparseArray(javaClass.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeSparseArray(childrenStates)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel) = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    private fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
        val childViewStates = SparseArray<Parcelable>()
        getChildren().forEach { child -> child.saveHierarchyState(childViewStates) }
        return childViewStates
    }

    private fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
        getChildren().forEach { child -> child.restoreHierarchyState(childViewStates) }
    }

    private fun ViewGroup.getChildren(): List<View> {
        val children = mutableListOf<View>()
        (0 until childCount).forEach {
            children.add(getChildAt(it))
        }
        return children
    }
}
