package com.altkamul.xpay.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.utils.roundToOneDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class InternetTestViewModel @Inject constructor(
    private val client: HttpClient
) : ViewModel() {

//    private val fakeUrl = "https://rr2---sn-n0q5uxanq-3gue.googlevideo.com/videoplayback?expire=1643057204&ei=1LvuYba2E5CDhgadyLeYBA&ip=37.19.197.231&id=o-AMBDSyaLBwb9epwsLUFDdV_so_mxrV9nJcj8hpcfHneE&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=3-OcqZ8Fei4UodZK5kCmy-oG&gir=yes&clen=10948870&ratebypass=yes&dur=163.561&lmt=1636121066679515&fexp=24001373,24007246&c=WEB&txp=5430434&n=OxBX4hzDdU1UOA&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhANA3h4xkUaIAfQIW0jyenw0dHSVA2U2CLfAa9HUgXwFEAiAaVYND5xWIkXSqx-KUt0evyB2_JUhaMFlY_ZIYwWJLBA%3D%3D&redirect_counter=1&rm=sn-p5qe7676&req_id=95104a869f12a3ee&cms_redirect=yes&ipbypass=yes&mh=cL&mip=197.252.217.250&mm=31&mn=sn-n0q5uxanq-3gue&ms=au&mt=1643035259&mv=m&mvi=2&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgDiv2E7RSdhuqhQ6RQ5keuAJJ-P_hX2FGtVUAPgr7xYsCIQCAfNkTdpkP_d2BIf_zNQ-cg6CZs2oJKTK4NWc1X8VUhA%3D%3D"
    private val fakeUrl = "https://lh3.googleusercontent.com/I9XAfd415Gsru-PKmesJvZDa2q7xyUt8cWiL1dxBTxXsdmn9o-jqKrCPSRdEqpNz435jMKeOMPZhbmPDN5AwvFoQoKntjCdB4CJ46g=w512-l90-sg-rj-c0xffffff"
    private var contentSize = 0L
    /** Speed value , MB/S */
    val speed = mutableStateOf(0.0)
    val unit = mutableStateOf("kb")
    val progress = mutableStateOf(0.0f)

    fun checkInternetSpeed(){
        val scope = viewModelScope
        scope.launch{
            val response: HttpResponse = client.get(urlString = fakeUrl){
                onDownload { downloaded, contentLength ->
                    contentSize = contentLength
                    progress.value = (downloaded / contentLength.toFloat()).coerceAtMost(1.0f)
                }
            }
            /** Catching the time that the download finished on */
            val finishedOn = Date().time
            /** Catching the time that the download started on */
            val startedOn = response.responseTime.toJvmDate().time
            /** Converting contentSize from bytes to kb by dividing by 1000 */
            val downloadedSize = contentSize.div(1000.0)
            /** Getting the time taken by download process */
            val timeTaken = (finishedOn - startedOn) / 1000.0
            Timber.d("start on $startedOn and finish on $finishedOn")
            Timber.d("content length is $contentSize and time taken for download is $timeTaken")

            /** Time to calculate the speed */
            speed.value = ceil(downloadedSize / timeTaken)

            /** Check if speed is > 1000, if so change it to mb instead of kb */
            if(speed.value >= 1000){
                speed.value = speed.value.div(1000).roundToOneDecimal()
                unit.value = "mb"
                /** Set progress to full 1.0 */
                progress.value = 1.0f
            }
        }
    }

    fun reCheckInternetSpeed(){
        viewModelScope.launch {
            speed.value = 0.0
            unit.value = "kb"
            progress.value = 0.0f
            contentSize = 0L
            delay(1000)
            checkInternetSpeed()
        }
    }
}