package layout

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.ViewPager
import android.support.v7.widget.CardView
import android.text.SpannableString
import android.text.style.UnderlineSpan
//import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kiolk.hrodnaday.ui.activites.MuseumActivity
import com.github.kiolk.hrodnaday.ui.activites.MuseumActivity.museumActivity.MUSEUM
import com.github.kiolk.hrodnaday.data.models.DayNoteModel
import com.github.kiolk.hrodnaday.ui.activites.PictureActivity
import com.github.kiolk.hrodnaday.R
import com.github.kiolk.hrodnaday.convertEpochTime
import com.github.kiolk.hrodnaday.data.recycler.setupPicture
import com.github.kiolk.hrodnaday.ui.fragments.PICTURE_URL
import kiolk.com.github.pen.Pen

class SlideEventsFragment : Fragment() {

    var day: Long? = 0
    lateinit var dayNote: DayNoteModel
    var dayType: Int? = 1

    fun formInstance(dayNote: DayNoteModel?, typeofDay: Int?): SlideEventsFragment {
        val day: SlideEventsFragment = SlideEventsFragment()
        val bundle: Bundle = Bundle()
        dayNote?.day?.let { bundle.putLong("day", it) }
        bundle.putSerializable("note", dayNote)
        typeofDay?.let { bundle.putInt("SizeOfArray", it) }
        day.arguments = bundle
        return day
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        day = arguments?.getLong("day")
        dayNote = arguments?.getSerializable("note") as DayNoteModel
        dayType = arguments?.getInt("SizeOfArray")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_screen_events, null) ?: super.onCreateView(inflater, container, savedInstanceState)
        val title = view?.findViewById<TextView>(R.id.day_one_event_card_text_view)
        val backToTodayView = view?.findViewById<TextView>(R.id.back_to_today_text_view)
        backToTodayView?.visibility = View.VISIBLE
        backToTodayView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val pager: ViewPager = view.parent as ViewPager

                pager.currentItem = pager.adapter.count.minus(2)
            }

        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view?.background = view?.context?.resources?.getDrawable(R.drawable.colorlees_background)
        }
        view?.isScrollContainer = true

        if (dayType == 0) {
            backToTodayView?.visibility = View.INVISIBLE
            title?.text = context.resources.getString(R.string.TOMORROW)
            view?.findViewById<TextView>(R.id.title_one_event_card_text_view)?.text = resources.getString(R.string.COME_BACK_TOMORROW)
            view?.findViewById<TextView>(R.id.creating_one_event_card_text_view)?.text = resources.getString(R.string.HRODNA_DAY_TEAM)
            Pen.getInstance().getImageFromUrl("https://img.tyt.by/n/regiony/09/1/02_geraldicheskiy_test_grodno.jpg").inputTo(view?.findViewById<ImageView>(R.id.picture_one_event_card_image_view))
            view?.findViewById<LinearLayout>(R.id.work_description_one_event_card_text_view)?.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view?.background = view?.context?.resources?.getDrawable(R.drawable.colorlees_background)
                view?.findViewById<CardView>(R.id.event_card_view)?.background = view?.context?.resources?.getDrawable(R.drawable.colorlees_background)
            }
            return view
        } else if (dayType == 1) {
            setUpNoteInView(view, dayNote)
            title?.text = context.resources.getString(R.string.TODAY)
            backToTodayView?.visibility = View.INVISIBLE
            return view
        }

        setUpNoteInView(view, dayNote)
        return view
    }
}

fun setUpNoteInView(view: View?, dayNote: DayNoteModel) {
    view?.findViewById<TextView>(R.id.day_one_event_card_text_view)?.text = convertEpochTime(dayNote.day, view?.context)
    view?.findViewById<TextView>(R.id.title_one_event_card_text_view)?.text = dayNote.title
    view?.findViewById<TextView>(R.id.author_one_event_card_text_view)?.text = dayNote.author
    view?.findViewById<TextView>(R.id.creating_one_event_card_text_view)?.text = dayNote.creating
    view?.findViewById<TextView>(R.id.description_one_event_card_text_view)?.text = dayNote.description
    view?.findViewById<TextView>(R.id.size_one_card_text_view)?.text = dayNote.size
    view?.findViewById<TextView>(R.id.material_one_card_text_view)?.text = dayNote.materials
    val spannableContent = SpannableString(dayNote.museum)
    spannableContent.setSpan(UnderlineSpan(), 0, spannableContent.length, 0)
    view?.findViewById<TextView>(R.id.museum_one_card_text_view)?.text = spannableContent
    view?.findViewById<TextView>(R.id.museum_one_card_text_view)?.setOnClickListener {
        val museumUrl = dayNote.museumUrl
        if (museumUrl.contains("http", true)) {
            openUrl(museumUrl, view.context)
        } else {
            openMuseumPage(dayNote.museum, view.context)
        }
    }

    view?.findViewById<TextView>(R.id.author_article_one_card_text_view)?.text = dayNote.articleAuthor

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        view?.background = view?.context?.resources?.getDrawable(R.drawable.colorlees_background)
        view?.findViewById<CardView>(R.id.event_card_view)?.background = view?.context?.resources?.getDrawable(R.drawable.colorlees_background)
    }
    val array: Array<DayNoteModel> = arrayOf(dayNote)
    view?.findViewById<ImageView>(R.id.picture_one_event_card_image_view)?.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent: Intent = Intent(view.context, PictureActivity::class.java)
            intent.putExtra(PICTURE_URL, dayNote.pictureUrl)
            startActivity(view.context, intent, null)
        }
    })
    view?.context?.let { setupPicture(0, view.findViewById(R.id.picture_one_event_card_image_view), array, it) }
}

fun openUrl(url: String, context: Context) {
    val browserIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(context, browserIntent, null)
}

fun openMuseumPage(museum: String, context: Context) {
    val intnt: Intent = Intent(context, MuseumActivity::class.java)
    intnt.putExtra(MUSEUM, museum)
    startActivity(context, intnt, null)
}