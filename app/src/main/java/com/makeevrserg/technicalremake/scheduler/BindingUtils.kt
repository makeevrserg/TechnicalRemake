package com.makeevrserg.technicalremake.scheduler

import android.view.View
import android.widget.Advanceable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.makeevrserg.technicalremake.R



//в RecyclerView ставит сломанную картинку если в плейлисте есть не прошедшие md5 файлы
@BindingAdapter("brokenImage")
fun ImageView.setBrokenImage(item: JsonParseClasses.AdvancedDay?) {
    item?.let {
        setImageResource(
            if (item.isBroken)
                R.drawable.ic_warn
            else
                R.drawable.ic_check
        )
    }
}


//Костыль для RecyclerView. Если proportion==null значит день пустой
@BindingAdapter("setDay")
fun LinearLayout.setDay(item: JsonParseClasses.AdvancedDay?) {
    item?.let {
        visibility = if (item.playlistProportion == null)
            View.GONE
        else
            View.VISIBLE
    }
}

@BindingAdapter("setProportion")
fun TextView.setProportion(item: JsonParseClasses.AdvancedDay?) {
    item?.let {
        text = if (item.playlistProportion == null)
            "0"
        else
            item.playlistProportion.toString()
    }
}


@BindingAdapter("playerActivity")
fun ImageView.setPlayerActivity(isPlay: Boolean) {
    setImageResource(
        if (isPlay)
            R.drawable.ic_pause
        else
            R.drawable.ic_play
    )
}