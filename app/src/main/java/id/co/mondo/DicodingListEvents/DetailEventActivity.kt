package id.co.mondo.DicodingListEvents

import DetailEventViewModel
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.co.mondo.aplikasidicodingevent.databinding.ActivityDetailEventBinding


class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels()

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId == -1) {
            Log.e("DetailEventActivity", "Invalid Event ID")
            return
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.getDetailEvent(eventId)
        viewModel.event.observe(this) { event ->
            event?.let { eventData ->
                binding.tvEventName.text = eventData.name
                binding.tvOwnerName.text = eventData.ownerName
                binding.tvDescription.text = HtmlCompat.fromHtml(eventData.description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
                Glide.with(this)
                    .load(eventData.mediaCover ?: eventData.imageLogo)
                    .into(binding.imgCover)
                val quotaRemaining = (eventData.quota ?: 0) - (eventData.registrants ?: 0)
                binding.tvQuota.text = "Sisa Kuota: $quotaRemaining"
                binding.tvBeginTime.text = "Waktu Mulai: ${eventData.beginTime ?: "Tidak tersedia"}"
                binding.btnLink.setOnClickListener {
                    val url = eventData.link.orEmpty()
                    if (url.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } else {
                        Log.e("DetailEventActivity", "Link is empty")
                    }
                }
            } ?: run {
                Log.e("DetailEventActivity", "Event data is null")
                showError("Event details not found")
            }
        }
    }


    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}


