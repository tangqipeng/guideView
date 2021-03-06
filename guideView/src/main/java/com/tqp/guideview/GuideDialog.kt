package com.tqp.guideview

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.view.KeyEvent
import android.view.WindowManager


/**
 * @author  tangqipeng
 * @date  4/19/21 11:47 AM
 * @email tangqipeng@aograph.com
 */
class GuideDialog(private var mContext: Context) {

    private var mTAG: String = GuideDialog::class.java.name

    private var mGuideParamterList: MutableList<GuideParamter> = mutableListOf()

    private var mStatus: KeyBackEnum = KeyBackEnum.DEFAULT
    private var mCurIndex: Int = 0
    private var mAmount: Float = 0F
    private var mLastStepListener: (() -> Unit)? = null

    private fun createDialog(guideParamter: GuideParamter) {
        val dialog = Dialog(mContext, R.style.GuideDialogStyle1)
        if (mStatus == KeyBackEnum.INVALID){
            dialog.setCancelable(false)
        } else if (mStatus == KeyBackEnum.EFFECTIVE) {
            dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
                override fun onKey(
                        dialog: DialogInterface?,
                        keyCode: Int,
                        event: KeyEvent?
                ): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        logd(mTAG, "keyCode is $keyCode")
                        dialog?.dismiss()
                        if (guideParamter.mTipHintListener != null) {
                            guideParamter.mTipHintListener?.invoke()
                        }
                        mCurIndex++
                        showGuide()
                        return true
                    }
                    return true
                }
            })
        }
        val view = GuideView(mContext)
        if (guideParamter.mView != null) {
            view.addGuideView(guideParamter.mView!!)
            if (guideParamter.mShape != null){
                view.setHighLightShape(guideParamter.mShape!!)

                if (guideParamter.mShape == Shape.RoundRect && (guideParamter.mCx != 0F || guideParamter.mCy != 0F)){
                    view.setRoundRectCorners(guideParamter.mCx, guideParamter.mCy)
                }
            }

            if (guideParamter.mPaddingOffset != null){
                view.setHighLightViewPadding(guideParamter.mPaddingOffset!!)
            }
            if (guideParamter.mTipView != null) {
                view.addTipContentView(guideParamter.mTipView!!)
                if (guideParamter.mOrientation != null) {
                    view.setTipViewOrientation(guideParamter.mOrientation!!)
                } else {
                    throw Exception("????????????????????????????????????????????????")
                }
                if (guideParamter.mTipMargin != null){
                    view.setTipViewMargin(guideParamter.mTipMargin!!)
                }
                view.setOnTipClickListener {
                    dialog.dismiss()
                    if (guideParamter.mTipHintListener != null){
                        guideParamter.mTipHintListener?.invoke()
                    }
                    mCurIndex ++
                    showGuide()
                }
                dialog.setContentView(view)
                guideParamter.mView!!.postDelayed({
                    val rect = Rect()
                    guideParamter.mView!!.getGlobalVisibleRect(rect)

                    val position = IntArray(2)
                    guideParamter.mView!!.getLocationOnScreen(position)
                    view.setViewPosition(position)

                    val wd = dialog.window
                    val lp = wd?.attributes
                    if (lp != null) {
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
                        lp.height = getHeight(mContext)
                    }
                    if (wd != null) {
                        if (guideParamter.mAmount != 0F) {
                            wd.setDimAmount(guideParamter.mAmount)//???????????????
                        } else {
                            wd.setDimAmount(mAmount)//???????????????
                        }
                        wd.attributes = lp
                    }
                    dialog.show()
                }, 200)
            }
        } else {
            loge(mTAG, "mView is null")
        }
    }

    fun showGuide() {
        if (mGuideParamterList.size > 0 && mCurIndex < mGuideParamterList.size){
            val guideParamter = mGuideParamterList[mCurIndex]
            createDialog(guideParamter)
        } else {
            logd(mTAG, "???????????????")
            if (this.mLastStepListener != null){
                this.mLastStepListener?.invoke()
            }
        }
    }

    /**
     * ????????????????????????
     */
    fun setGuideParamters(guideParamters: MutableList<GuideParamter>){
        this.mGuideParamterList.addAll(guideParamters)
    }

    /**
     * ????????????????????????????????????
     */
    fun addGuideParamter(guideParamter: GuideParamter){
        this.mGuideParamterList.add(guideParamter)
    }

    /**
     * ??????dialog??????????????????
     */
    fun setDialogDimAmount(amount:Float) {
        this.mAmount = amount
    }

    /**
     * ?????????????????????
     */
    fun setKeyBackStatus(status: KeyBackEnum) {
        this.mStatus = status
    }

    /**
     * ??????????????????????????????
     */
    fun setGuideLastStepListener(lastStepListener:() -> Unit){
        this.mLastStepListener = lastStepListener
    }

}
