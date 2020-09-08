package com.natura.android.dialog

import android.content.Context
import android.content.DialogInterface
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.natura.android.R

class DialogAlert @JvmOverloads constructor (
    private val context: Context,
    private val dialogContent: String,
    private val mainButtonTitle: String,
    private val mainButtonAction: DialogInterface.OnClickListener,
    //tratar no builder da alertDialog quando nulo
    private val secondaryButtonTitle: String? = null,
    private val secondaryButtonAction: DialogInterface.OnClickListener? = null,
    private val isCancelable: Boolean = true,
    private val dialogTheme: Int? = null
) {
    lateinit var alertDialog: AlertDialog

    fun create(): DialogAlert {
        alertDialog = AlertDialog.Builder(context, resolveThemeResource()).create().apply {
            setTitle(dialogContent)
            setButton(DialogInterface.BUTTON_POSITIVE, mainButtonTitle, mainButtonAction)
            setCancelable(isCancelable)
        }
        return this
    }

    fun show() {
        alertDialog.show()
    }

    private fun resolveThemeResource(): Int {
        val dialogThemeResource = TypedValue()

        dialogTheme?.let {
            context.setTheme(dialogTheme)
        }

        context.theme.resolveAttribute(R.attr.dialogAlertTheme, dialogThemeResource, true)

        return dialogThemeResource.resourceId
    }
}
