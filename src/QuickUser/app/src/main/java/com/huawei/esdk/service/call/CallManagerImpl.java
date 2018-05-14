package com.huawei.esdk.service.call;

import java.util.List;

import common.AuthType;
import common.DeviceStatus;
import common.TupCallNotify;
import object.Conf;
import object.DecodeSuccessInfo;
import object.KickOutInfo;
import object.NetAddress;
import object.OnLineState;
import object.TupAudioQuality;
import object.TupAudioStatistic;
import object.TupCallLocalQos;
import object.TupCallQos;
import object.TupMsgWaitInfo;
import object.TupRegisterResult;
import object.TupServiceRightCfg;
import object.TupUnSupportConvene;
import object.TupVideoQuality;
import object.TupVideoStatistic;
import tupsdk.TupCall;

/**
 * Created on 2017/3/30.
 */
public class CallManagerImpl implements TupCallNotify
{
    @Override
    public void onCallComing(TupCall tupCall)
    {

    }

    @Override
    public void onRegisterResult(TupRegisterResult tupRegisterResult)
    {

    }

    @Override
    public void onCallStartResult(TupCall tupCall)
    {

    }

    @Override
    public void onCallGoing(TupCall tupCall)
    {

    }

    @Override
    public void onCallRingBack(TupCall tupCall)
    {

    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo)
    {

    }

    @Override
    public void onCallConnected(TupCall tupCall)
    {

    }

    @Override
    public void onCallEnded(TupCall tupCall)
    {

    }

    @Override
    public void onCallDestroy(TupCall tupCall)
    {

    }

    @Override
    public void onCallRTPCreated(TupCall tupCall)
    {

    }

    @Override
    public void onCallAddVideo(TupCall tupCall)
    {

    }

    @Override
    public void onCallDelVideo(TupCall tupCall)
    {

    }

    @Override
    public void onCallViedoResult(TupCall tupCall)
    {

    }

    @Override
    public void onCallRefreshView(TupCall tupCall)
    {

    }

    @Override
    public void onMobileRouteChange(TupCall tupCall)
    {

    }

    @Override
    public void onAudioEndFile(int i)
    {

    }

    @Override
    public void onNetQualityChange(TupAudioQuality tupAudioQuality)
    {

    }

    @Override
    public void onStatisticNetinfo(TupAudioStatistic tupAudioStatistic)
    {

    }

    @Override
    public void onStatisticMos(int i, int i1)
    {

    }

    @Override
    public void onNotifyQosinfo(TupCallQos tupCallQos)
    {

    }

    @Override
    public void onNotifyLocalQosinfo(TupCallLocalQos tupCallLocalQos)
    {

    }

    @Override
    public void onVideoOperation(TupCall tupCall)
    {

    }

    @Override
    public void onVideoStatisticNetinfo(TupVideoStatistic tupVideoStatistic)
    {

    }

    @Override
    public void onVideoQuality(TupVideoQuality tupVideoQuality)
    {

    }

    @Override
    public void onVideoFramesizeChange(TupCall tupCall)
    {

    }

    @Override
    public void onSessionModified(TupCall tupCall)
    {

    }

    @Override
    public void onSessionCodec(TupCall tupCall)
    {

    }

    @Override
    public void onCallHoldSuccess(TupCall tupCall)
    {

    }

    @Override
    public void onCallHoldFailed(TupCall tupCall)
    {

    }

    @Override
    public void onCallUnHoldSuccess(TupCall tupCall)
    {

    }

    @Override
    public void onCallUnHoldFailed(TupCall tupCall)
    {

    }

    @Override
    public void onCallBldTransferRecvSucRsp(TupCall tupCall)
    {

    }

    @Override
    public void onCallBldTransferSuccess(TupCall tupCall)
    {

    }

    @Override
    public void onCallBldTransferFailed(TupCall tupCall)
    {

    }

    @Override
    public void onCallAtdTransferSuccess(TupCall tupCall) {

    }

    @Override
    public void onCallAtdTransferFailed(TupCall tupCall) {

    }


    @Override
    public void onSetIptServiceSuc(int i)
    {

    }

    @Override
    public void onSetIptServiceFal(int i)
    {

    }

    @Override
    public void onSipaccountWmi(List<TupMsgWaitInfo> list)
    {

    }

    @Override
    public void onServiceRightCfg(List<TupServiceRightCfg> list)
    {

    }

    @Override
    public void onVoicemailSubSuc()
    {

    }

    @Override
    public void onVoicemailSubFal()
    {

    }

    @Override
    public void onImsForwardResult(List<String> list)
    {

    }

    @Override
    public void onCallUpateRemoteinfo(TupCall tupCall)
    {

    }

    @Override
    public void onNotifyNetAddress(NetAddress netAddress)
    {

    }

    @Override
    public void onDataReady(int i, int i1)
    {

    }

    @Override
    public void onBFCPReinited(int i)
    {

    }

    @Override
    public void onDataSending(int i)
    {

    }

    @Override
    public void onDataReceiving(int i)
    {

    }

    @Override
    public void onDataStopped(int i)
    {

    }

    @Override
    public void onDataStartErr(int i, int i1)
    {

    }

    @Override
    public void onLineStateNotify(OnLineState onLineState)
    {

    }

    @Override
    public void onDataFramesizeChange(TupCall tupCall)
    {

    }

    @Override
    public void onDecodeSuccess(DecodeSuccessInfo decodeSuccessInfo)
    {

    }

    @Override
    public void onOnLineStateResult(int i, int i1)
    {

    }

    @Override
    public void onOnSRTPStateChange(int i, int i1)
    {

    }

    @Override
    public void onPasswordChangedResult(int i)
    {

    }

    @Override
    public void onGetLicenseTypeResult(int i, int i1)
    {

    }

    @Override
    public void onApplyLicenseResult(int i)
    {

    }

    @Override
    public void onRefreshLicenseFailed()
    {

    }

    @Override
    public void onReleaseLicenseResult(int i)
    {

    }

    @Override
    public void onIdoOverBFCPSupport(int i, int i1)
    {

    }

    @Override
    public void onDeviceStatusNotify(DeviceStatus deviceStatus)
    {

    }

    @Override
    public void onAuthorizeTypeNotify(int i, AuthType authType)
    {

    }

    @Override
    public void onReferNotify(int i)
    {

    }

    @Override
    public void onCallDialoginfo(int i, String s, String s1, String s2)
    {

    }

    @Override
    public void onConfNotify(int i, Conf conf)
    {

    }

    @Override
    public void onCtdInfo(TupCall tupCall)
    {

    }

    @Override
    public void onBeTransferToPresenceConf(TupCall tupCall)
    {

    }

    @Override
    public void onUnSupportConvene(TupUnSupportConvene tupUnSupportConvene)
    {

    }

    @Override
    public void onNotifyLogOut()
    {

    }

    @Override
    public void onNoStream(int i, int i1) {

    }

    @Override
    public void onVideoTmmbrSwitch(int i, int i1) {
        
    }

    @Override
    public void onAudioHowlStatus(int i, int i1) {

    }

    @Override
    public void onAudioResetRoute(int i, int i1) {

    }

    @Override
    public void onCallHMEInterfaceErrorInfo(String s, int i) {

    }

}
