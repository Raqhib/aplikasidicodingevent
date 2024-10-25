package id.co.mondo.DicodingListEvents.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.co.mondo.DicodingListEvents.data.response.ListEventsResponse
import id.co.mondo.DicodingListEvents.data.retrofit.ApiConfig
import id.co.mondo.DicodingListEvents.ui.adapter.ListEventsAdapter
import id.co.mondo.aplikasidicodingevent.databinding.FragmentFinishedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListEventsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListEventsAdapter()
        binding.rvItem.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvItem.adapter = adapter
        fetchEvents()
    }

    private fun fetchEvents() {
        showLoading(true)
        val apiService = ApiConfig.getApiService()
        val call = apiService.getEvents(0)

        call.enqueue(object : Callback<ListEventsResponse> {
            override fun onResponse(call: Call<ListEventsResponse>, response: Response<ListEventsResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    Log.d("FinishedFragment", "Data received: ${response.body()}")
                    val events = response.body()?.listEvents
                    if (!events.isNullOrEmpty()) {
                        adapter.submitList(events)
                    } else {
                        showError("Data kosong")
                    }
                } else {
                    showError("Gagal mengambil data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEventsResponse>, t: Throwable) {
                showLoading(false)
                showError("Gagal terhubung ke server: ${t.message}")
                Log.e("FinishedFragment", "Error fetching events", t)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
