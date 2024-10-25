import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.co.mondo.DicodingListEvents.data.response.BaseResponse
import id.co.mondo.DicodingListEvents.data.response.DetailEventResponse
import id.co.mondo.DicodingListEvents.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {

    private val _event = MutableLiveData<DetailEventResponse?>()
    val event: LiveData<DetailEventResponse?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailEvent(id: Int) {
        _isLoading.value = true  // Tampilkan loading saat mulai mengambil data
        Log.d("DetailEventViewModel", "Fetching details for ID: $id")
        val client = ApiConfig.getApiService().getDetailEvent(id.toString())
        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                _isLoading.value = false  // Sembunyikan loading setelah data selesai diambil
                if (response.isSuccessful) {
                    _event.value = response.body()?.event
                } else {
                    _event.value = null
                    Log.e("DetailEventViewModel", "Error response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false  // Sembunyikan loading jika terjadi kegagalan
                _event.value = null
                Log.e("DetailEventViewModel", "Failure: ${t.message}")
            }
        })
    }
}
