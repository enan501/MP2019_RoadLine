package konkukSW.MP2019.roadline.UI.photo


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import konkukSW.MP2019.roadline.Data.Adapter.MoneyGridAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Money

import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {
    lateinit var v:View
    var imgSrc = ""
//    var date = Date()
//    var isAll = false
    private var photoId = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_image, container, false)
        init()
        return v
    }

    fun init(){
        initData()
        initLayout()
    }

    fun initData(){
        val bundle = arguments
        if(bundle != null){
            imgSrc = bundle.getString("imgSrc")
            photoId = bundle.getString("photoId")
        }
    }

    fun initLayout(){
        val imageView = v.findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgSrc))
    }

    fun getPhotoId() : String{
        return photoId
    }

}
