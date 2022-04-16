package com.diariodobebe.ui.main_activity.report

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentReportBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Sleep
import com.diariodobebe.ui.main_activity.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private var document: PdfDocument? = null
    private var file: File? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                writeReport(document, file)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(layoutInflater)
        val root = binding.root


        binding.btnReportDownload.setOnClickListener {
            binding.btnReportDownload.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            generatePDF()
        }

        return root
    }

    private fun generatePDF() {
        MainScope().launch {
            val title: String = getString(R.string.sleep_report)
            var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.sleep_header)

            bitmap = Bitmap.createScaledBitmap(bitmap, 1200, 518, false)

            document = PdfDocument()
            val paint = Paint()
            val paintTitle = Paint()

            val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, 1).create()
            val page = document!!.startPage(pageInfo)
            val canvas = page.canvas

            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            paintTitle.textAlign = Paint.Align.LEFT
            paintTitle.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paintTitle.textSize = 70f
            canvas.drawText(
                getString(R.string.app_name),
                ((pageInfo.pageWidth / 2) - 100).toFloat(),
                200f,
                paintTitle
            )
            paint.textSize = 60f
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(title, ((pageInfo.pageWidth / 2) - 100).toFloat(), 370f, paint)
            paintTitle.textAlign = Paint.Align.LEFT
            paint.textAlign = Paint.Align.LEFT


            paint.typeface = Typeface.DEFAULT
            paint.textSize = 35f
            paint.color = Color.BLACK

            val baby = GetBaby.getBaby(GetBaby.getBabyFile(requireContext()))
            canvas.drawText(getString(R.string.baby_name_template, baby.name), 20f, 590f, paint)
            //canvas.drawText(
                /*getString(
                    R.string.baby_age_template,
                    ((requireActivity() as MainActivity).binding.navView.getHeaderView(0)
                        .findViewById<TextView>(R.id.tv_baby_age)).text
                ), 20f, 640f, paint*/


            val cal = Calendar.getInstance()
            val df = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            df.timeZone = TimeZone.getTimeZone("UTC")

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                getString(R.string.baby_age_template, df.format(cal.time)),
                pageInfo.pageWidth - 20f,
                590f,
                paint
            )

            paint.textAlign = Paint.Align.LEFT
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.strokeWidth = 4f
            paint.color = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)


            if (baby.entryList != null) {
                canvas.drawRect(20f, 780f, (pageInfo.pageWidth - 20).toFloat(), 860f, paint)

                paint.style = Paint.Style.FILL
                paint.strokeWidth = 1f
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.color = ContextCompat.getColor(requireContext(), R.color.white)
                canvas.drawText(
                    getString(R.string.sleep),
                    (pageInfo.pageWidth / 2).toFloat(),
                    830f,
                    paint
                )
                paint.color = ContextCompat.getColor(requireContext(), R.color.black)
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(getString(R.string.total_sleep_time), (20).toFloat(), 920f, paint)
                canvas.drawText(getString(R.string.avg_sleep_time), (20).toFloat(), 990f, paint)
                var entryCountValue = 0
                var totalSleepTime = 0

                baby.entryList!!.forEach {
                    if (it.type == Entry.EntryType.ENTRY_SLEEP) {
                        val sleep = it as Sleep
                        entryCountValue += 1
                        totalSleepTime += sleep.duration ?: 0
                    }

                }

                var avgSleep = 0

                if (entryCountValue > 0) {
                    avgSleep = totalSleepTime / entryCountValue
                }

                paint.textAlign = Paint.Align.RIGHT

                canvas.drawText(
                    totalSleepTime.toString() + " " + getString(R.string.minutes),
                    (pageInfo.pageWidth - 20).toFloat(),
                    920f,
                    paint
                )
                canvas.drawText(
                    avgSleep.toString() + " " + getString(R.string.minutes),
                    (pageInfo.pageWidth - 20).toFloat(),
                    990f,
                    paint
                )
                canvas.drawText(
                    getString(R.string.sleep_entry_count, entryCountValue.toString()),
                    (pageInfo.pageWidth - 20).toFloat(),
                    1060f,
                    paint
                )


                document!!.finishPage(page)
                file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    getString(R.string.sleep_report) + ".pdf"
                )
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (requireActivity().checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            writeReport(document, file)
                        }
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                binding.progressBar.visibility = View.GONE
                binding.btnReportDownload.visibility = View.VISIBLE
            }

        }
    }

    private fun writeReport(document: PdfDocument?, file: File?) {
        if (document != null && file != null) {
            document.writeTo(file.outputStream())
            Toast.makeText(
                requireContext(),
                getString(R.string.success_report),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}