import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.dxcustomlibrary.R
import com.example.dxcustomlibrary.visible
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

/*
            ██████╗░██╗░░██╗░█████╗░██╗░░░██╗░██████╗████████╗░█████╗░███╗░░░███╗
            ██╔══██╗╚██╗██╔╝██╔══██╗██║░░░██║██╔════╝╚══██╔══╝██╔══██╗████╗░████║
            ██║░░██║░╚███╔╝░██║░░╚═╝██║░░░██║╚█████╗░░░░██║░░░██║░░██║██╔████╔██║
            ██║░░██║░██╔██╗░██║░░██╗██║░░░██║░╚═══██╗░░░██║░░░██║░░██║██║╚██╔╝██║
            ██████╔╝██╔╝╚██╗╚█████╔╝╚██████╔╝██████╔╝░░░██║░░░╚█████╔╝██║░╚═╝░██║
            ╚═════╝░╚═╝░░╚═╝░╚════╝░░╚═════╝░╚═════╝░░░░╚═╝░░░░╚════╝░╚═╝░░░░░╚═╝
 */
/**
 * @author Julio Landazuri Diaz
 * @version 4.0
 *
 * Esta clase te permite mostrar dialogos con una vista personalizada.
 *
 *            ¡Ahora con colores y notificaciones!
 *                   ¡Y loaders ╰(*°▽°*)╯!
 */
/* ------- Ejemplo de uso CREATEDIALOG -------
        DxCustom(this@LoginActivity)
                .createDialog()
                .setTitulo("Titulo", context.getColor(R.color.dxCustom), 5f)
                .setMensaje("Mensaje", context.getColor(R.color.dxCustom), 5f)
                .setIcono(color = context.getColor(R.color.dxCustom))
                .noPermitirSalirSinBotones()
                .showAceptarButton("aceptar personalizado", context.getColor(R.color.red)) {
                    Log.d(":::", "Aceptar pulsado.")
                }
                .showCancelarButton (
                    "cancelar personalizado",
                    strokecolor = R.color.colorAccent,
                    textColor = context.getColor(R.color.darkRed)
                ) {
                    Log.d(":::", "Cancelar pulsado.")
                }
                .addCustomView(bindingCustomView.root)
                .showDialog()
  ------- Ejemplo de uso CREATENOTIFICATION -------
        DxCustom(context).createNotification(parentView,
            "Mensaje de ejemplo",
            backgroundColor = context.getColor(R.color.lightGreen),
            strokeColor = context.getColor(R.color.red),
        )
  ------- Ejemplo de uso CREATELOADING -------
        DxCustom(context).createLoading(
            R.raw.anim,
            "Cargando...",
            "Por favor espere...",
            -50,
            -10,
            false
        )
 */
const val DELAY_TIME = 500L
const val ERROR_TAG = "DxCustom"
class DxCustom(
    private val context: Context? = null
) {
    /* ------- Ojo con las IDs!! -------
        Para que DxCustom funcione correctamente, se deben respetar las IDs de los elementos del XML.
        Para ver las IDs que se usan ver la funcion ->       loadLayoutComponentes()
    */

    //DxCustom AppCompat
    private var parentDxCustomLayout:   LinearLayout?   = null
    private var acceptButtonLayout:     Button?         = null
    private var cancelButtonLayout:     Button?         = null
    private var tituloDxCustomLayout:   TextView?       = null
    private var mensajeDxCustomLayout:  TextView?       = null
    private var iconoDxCustomLayout:    ImageView?      = null
    private var customViewUpLayout:     LinearLayout?   = null
    private var customViewDownLayout:   LinearLayout?   = null

    private var tituloDxCustom:                 String              = "Sin titulo."
    private var mensajeDxCustom:                String              = "Sin mensaje."
    private var iconoDxCustom:                  Drawable?           = null
    private var layoutDxCustomPersonalizadoCenter:      XmlResourceParser?  = null
    private var layoutDxCustomPersonalizadoBottom:      XmlResourceParser?  = null
    private lateinit var dialog:                Dialog

    private var tituloColor:    Int? = null
    private var mensajeColor:   Int? = null
    private var iconoColor:     Int? = null

    private var tituloSp:       Float? = null
    private var mensajeSp:      Float? = null

    private var animarAlSalir: Boolean = true
    private var animarAlEsconder: Boolean = true

    private var selectedGravity: Int = Gravity.BOTTOM

    /**
     * Permite crear una pantalla de carga, con un lottie, titulo y mensaje.
     *
     * @param lottie Lottie a mostrar en la pantalla de carga, si es un lottie a mostrar con titulo y mensaje, puede que tengas que editar el tamaño en el Lottie Editor.
     * @param titulo Titulo a mostrar en la pantalla de carga.
     * @param mensaje Mensaje a mostrar en la pantalla de carga.
     * @param textoMarginTop Margen superior del texto (permite valores negativos).
     * @param lottieMarginTop Margen superior del lottie (permite valroes negativos).
     * @param tituloColor Color del titulo.
     * @param mensajeColor Color del mensaje.
     * @param tituloSize Tamaño del titulo.
     * @param mensajeSize Tamaño del mensaje.
     * @param soloAnimacion Si es true, no se muestra el titulo ni el mensaje, solo la animacion a pantalla completa.
     *
     * @return Devuelve un objeto de tipo Dialog, para poder cerrarlo cuando quieras deberas usar el .dimiss().
     */
    fun createLoading(
        lottie: Int = R.raw.default_loading_anim,
        titulo: String = "Cargando...",
        mensaje: String = "Espere un momento por favor.",
        textoMarginTop: Int = 0,
        lottieMarginTop: Int = 0,
        tituloColor: Int = -16777216,
        mensajeColor: Int = -16777216,
        backgroundColor: Int = -16777216,
        dividerColor: Int = -16777216,
        tituloSize: Float = 19f,
        mensajeSize: Float = 19f,
        startLottieFrame: Int = 0,
        autoPlayLottie: Boolean = true,
        loopLottie: Boolean = true
    ) {

        dialog =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Dialog(context!!, R.style.DxCustomBlured)
        }else{
            Dialog(context!!, R.style.DxCustom)
        }

        val inflater = LayoutInflater.from(context)

        try{

            val dialogView: View = inflater.inflate(R.layout.dx_custom_default_layout_loading_original, null)

            with(dialogView){

                val cardViewLayoutLoading = findViewById<MaterialCardView>(R.id.cardViewLayoutLoading)
                val divider = findViewById<View>(R.id.dividerColorDxCustomLoading)

                cardViewLayoutLoading.setCardBackgroundColor(backgroundColor)
                divider.setBackgroundColor(dividerColor)

                cardViewLayoutLoading.visibility = VISIBLE

                val lottieLinearLayoutLoading = findViewById<LinearLayout>(R.id.lottieLinearLayoutLoading)
                val tituloMensajeLayout = findViewById<LinearLayout>(R.id.tituloMensajeLayoutLoading)
                val tituloLayout = findViewById<TextView>(R.id.tituloDxCustomLoading)
                val mensajeLayout = findViewById<TextView>(R.id.mensajeDxCustomLoading)

                val lottieLayout = findViewById<LottieAnimationView>(R.id.animationView)

                val paramsLottie = lottieLinearLayoutLoading.layoutParams as RelativeLayout.LayoutParams
                paramsLottie.setMargins(0, lottieMarginTop, 0, 0)

                val params = tituloMensajeLayout.layoutParams as RelativeLayout.LayoutParams
                params.setMargins(0, textoMarginTop, 0, 0)
                tituloMensajeLayout.layoutParams = params

                lottieLayout.setAnimation(lottie)

                tituloLayout.setTextColor(tituloColor)
                tituloLayout.textSize = tituloSize
                mensajeLayout.setTextColor(mensajeColor)
                mensajeLayout.textSize = mensajeSize

                tituloLayout.text = titulo
                mensajeLayout.text = mensaje

                if(autoPlayLottie)
                    lottieLayout.playAnimation()

                lottieLayout.loop(loopLottie)
                lottieLayout.frame = startLottieFrame

            }

            dialog.setContentView(dialogView)

            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

            dialog.setCancelable(false)

            DxCustomLoader.displayLoader(dialog)

        }catch (e: Exception){

            Log.e(ERROR_TAG, e.printStackTrace().toString())
        }

    }

    /**
     * Crea una notificacion en la parte superior de la pantalla.
     *
     * @param parentLayout Layut padre de la notificacion, suele ser binding.root.
     * @param text Mensaje a mostrar.
     * @param textColor Color del mensaje.
     * @param backgroundColor Color de fondo de la notificacion, por defecto es blanco.
     * @param strokeColor Color del borde de la notificacion, por defecto es del mismo color que backgroundColor.
     * @param length Duracion de la notificacion, por defecto es Snackbar.LENGTH_SHORT.
     *
     */
    fun createNotification(
        parentLayout: View,
        text: String,
        textColor: Int = -16777216,
        backgroundColor: Int = -1,
        strokeColor: Int = backgroundColor,
        length: Int = Snackbar.LENGTH_SHORT
    ) {

        val snackbar: Snackbar = Snackbar.make(parentLayout, text, length)
        val view = snackbar.view
        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP

        view.setBackgroundColor(backgroundColor)

        view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(textColor)

        val shape = GradientDrawable()
        shape.cornerRadius = 10f
        shape.setColor(backgroundColor)
        shape.setStroke(2, strokeColor)
        view.background = shape

        view.layoutParams = params
        snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbar.show()

    }

    /**
     * Crea y asigna las configuraciones del dialogo.
     *
     * @param fullScreen Si se quiere que el dialogo sea de tamaño completo.
     * @param animarAlSalir Si se quiere que la animacion sea vertical o la predefinida de Dialog al mostrarse.
     * @param animarAlEsconder Si se quiere que la animacion sea vertical o la predefinida de Dialog al esconderse.
     * @param gravity Puede elegirse una de las siguientes opciones: Gravity.BOTTOM -> se muestra abajo, Gravity.CENTER -> se muestra en el centro.
     *
     * @return DxCustom
     */
    fun createDialog(
        fullScreen: Boolean = false,
        animarAlSalir: Boolean = true,
        animarAlEsconder: Boolean = true,
        backgroundColor: Int? = null,
        dividerColor: Int? = null,
        gravity: Int = selectedGravity
    ): DxCustom {

        verificarExistenciaDeRecursos()

        this.animarAlSalir = animarAlSalir
        this.animarAlEsconder = animarAlEsconder

        selectedGravity = gravity

        dialog =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Dialog(context!!, R.style.DxCustomBlured)
        }else{
            Dialog(context!!, R.style.DxCustom)
        }

        val inflater = LayoutInflater.from(context)

        try{
            var dialogView: View? = null

            when(selectedGravity){
                Gravity.CENTER -> {
                    dialogView = inflater.inflate(layoutDxCustomPersonalizadoCenter, null)
                    backgroundColor?.let {color ->
                        dialogView?.findViewById<CardView>(R.id.cardViewCenter)?.setCardBackgroundColor(color)
                    }

                    dividerColor?.let{
                        dialogView?.findViewById<View>(R.id.dividerDxCustom)?.setBackgroundColor(it)
                    }

                }
                Gravity.BOTTOM -> {
                    dialogView = inflater.inflate(layoutDxCustomPersonalizadoBottom, null)
                    backgroundColor?.let{ color ->
                        dialogView?.findViewById<LinearLayout>(R.id.parte_de_abajo)?.backgroundTintList = ColorStateList.valueOf(color)
                        dialogView?.findViewById<View>(R.id.circulo_de_icono_fondo)?.backgroundTintList = ColorStateList.valueOf(color)
                    }
                    dividerColor?.let{
                        dialogView?.findViewById<View>(R.id.lineaDxCustom)?.setBackgroundColor(it)
                    }
                }
            }

            dialogView?.let{

                dialog.setContentView(it)
            }?:run{
                throwNullPointerException("Customlayout no encontrado.")
            }

        }catch (e: Exception){
            Log.e(ERROR_TAG, e.printStackTrace().toString())
        }

        //Para que el layout que se asigna al DxCustom, este en pantalla completa.
        if(fullScreen)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        val window = dialog.window

        window?.let {

            val wlp = it.attributes

            wlp.gravity = Gravity.BOTTOM
        }?: run {

            throwIllegalStateException("Error al crear la ventana del dialogo.")

        }

        loadLayoutComponentes()

        return this
    }

    /**
     * Permite asignar el titulo del dialogo.
     *
     * @throws IllegalArgumentException
     *
     * @param titulo Texto que se asigna al titulo.
     * @param color Color del titulo.
     * @param textSize Size del titulo.
     *
     * @return DxCustom
     */
    fun setTitulo(titulo: String = tituloDxCustom, color: Int = -16777216, textSize: Float = 19f): DxCustom {

        if(titulo.isEmpty())
            throwIllegalArgumentException("El titulo proporcionado no es valido.")
        else{
            tituloDxCustom = titulo
        }

        tituloColor = color
        tituloSp = textSize

        return this
    }

    /**
     * Permite asignar un mensaje al dialogo.
     *
     * @param mensaje Texto que se asigna al mensaje.
     * @param color Color del mensaje.
     * @param textSize Size del mensaje.
     *
     * @return DxCustom
     */
    fun setMensaje(mensaje: String? = mensajeDxCustom, color: Int = -16777216, textSize: Float = 16f): DxCustom {

        mensaje?.let { msj ->
            mensajeDxCustom = msj
        } ?: run {
            mensajeDxCustomLayout?.visibility = View.GONE
        }

        mensajeColor = color
        mensajeSp = textSize

        return this
    }

    /**
     * Permite asginar un icono al dialogo y un color.
     *
     * @throws IllegalArgumentException
     *
     * @param icono Drawable a mostrar en el icono del dialogo (obligatorio).
     * @param color Color del icono, si el valor es NULL se respetara el color de icono.
     *
     * @return DxCustom
     */
    fun setIcono(
        icono: Drawable? = iconoDxCustom,
        color: Int? = context!!.resources.getColor(R.color.colorPrimary, null)
    ): DxCustom {

        icono?.let {
            iconoDxCustom = icono
        }?:run{

            Log.e(ERROR_TAG, "El icono proporcionado no es valido, se omitirá el icono.")

            iconoDxCustomLayout?.visibility = GONE
        }

        color?.let {
            iconoColor = color
        }

        return this
    }

    /**
     * Configura el dialogo para que solo se pueda cerrar mediante los botones, recuerda, tienes que mostrarlos.
     *
     * @return DxCustom
     */
    fun noPermitirSalirSinBotones(): DxCustom {

        dialog.setCancelable(false)

        return this
    }

    /**
     * Configura el dialogo para que se pueda cerrar mediante el boton de 'volver atras' o pulsando sobre la zona oscurecida.
     *
     * @return DxCustom
     */
    fun permitirSalirSinBotones(): DxCustom {

        dialog.setCancelable(true)

        return this
    }

    /**
     * Muestra el boton de aceptar y luego ejecuta onAccept.
     *
     * @param texto Permite sustituir el texto mostrado en el boton aceptar.
     * @param color Permite sustituir el color del boton aceptar.
     * @param onAccept Codigo proporcionado para que se ejecuta cuando el boton aceptar sea pulsado.
     *
     * @return DxCustom
     */
    fun showAceptarButton(
        texto: String? = "Aceptar",
        color: Int = context!!.resources.getColor(R.color.colorPrimary, null),
        textColor:  Int = context!!.resources.getColor(R.color.colorPrimary, null),
        textAllCaps: Boolean = true,
        onAccept: () -> Unit): DxCustom {

        acceptButtonLayout?.let { acceptBtn ->

            with(acceptBtn) {

                visibility = VISIBLE
                text = texto

                background = ContextCompat.getDrawable(context, R.drawable.default_button_shape)
                backgroundTintList = ColorStateList.valueOf(color)

                if(textColor != context.resources.getColor(R.color.colorPrimary, null)){

                    setTextColor(textColor)

                }else{
                    setTextColor(Color.WHITE)
                }

                isAllCaps = textAllCaps

                setOnClickListener {
                    animateDialogOnHide()
                    onAccept.invoke()
                    isEnabled = false
                    postDelayed({ isEnabled = true }, DELAY_TIME)
                }

            }

        }?:run{

            throwNullPointerException("No se encontro: acceptButtonLayout.")
        }

        return this
    }

    /**
     * Muestra el boton de cancelar y luego ejecuta onCancel.
     * Ademas, el boton de cancelar tiene un pequeño retraso para que no pueda ser accionado
     * y desencadenar 2 veces la acción.
     *
     * @param texto Permite sustituir el texto mostrado en el boton cancelar.
     * @param strokeColor Permite sustituir el color del borde del boton cancelar.
     * @param textColor Permite sustituir el color del textp del boton cancelar, por defecto es del color del strokeColor.
     * @param onCancel Codigo proporcionado para que se ejecuta cuando el boton cancelar sea pulsado.
     *
     * @return DxCustom
     */
    fun showCancelarButton(
        texto: String? = "Cancelar",
        strokecolor:  Int = context!!.resources.getColor(R.color.colorPrimary, null),
        textColor:  Int = context!!.resources.getColor(R.color.colorPrimary, null),
        textAllCaps: Boolean = true,
        textSize: Float = 16f,
        onCancel: () -> Unit
    ): DxCustom {

        cancelButtonLayout?.let { cancelBtn ->

            with (cancelBtn) {

                // add margin vertical to the button
                visibility = VISIBLE
                text = texto

                isAllCaps = textAllCaps

                background = ContextCompat.getDrawable(context, R.drawable.stroke_shape)
                backgroundTintList = ColorStateList.valueOf(strokecolor)

                if(textColor != context.resources.getColor(R.color.colorPrimary, null)){

                    setTextColor(textColor)
                }else{
                    setTextColor(strokecolor)
                }

                setOnClickListener {
                    animateDialogOnHide()
                    onCancel.invoke()
                    isEnabled = false
                    postDelayed({ isEnabled = true }, DELAY_TIME)
                }

            }

        }?:run{
            throwNullPointerException("No se encontro: cancelButtonLayout.")
        }

        return this
    }

    /**
     * Permite añadir una vista personalizada al dialogo.
     *
     * @throws NullPointerException
     *
     * @param customView Layout personalizado que se añade al layout del dialogo, recuerda,
     * para que puedas seguir jugando con los elementos, pasa 'binding.root' como customView.
     *
     * @param position Permite decidir si quieres que el customView este encima o debajo de los botones de cancelar y aceptar.
     *
     * @return DxCustom
     */
    fun addCustomView(customView: View, position: Boolean = true): DxCustom {

        var parentCustomView: LinearLayout? = null

        if (position){

            customViewUpLayout?.let {
                parentCustomView = it
            }?:run {

                throwNullPointerException("No se encontro customViewUpLayout.")
            }

        }
        else{
            customViewDownLayout?.let {
                parentCustomView = it
            }?:run {

                throwNullPointerException("No se encontro customViewDownLayout.")
            }
        }

        try{
            parentCustomView?.addView(customView) ?:run{
                throwNullPointerException("Error al crear customView.")
            }
        }catch (e: Exception){

            Log.e("DxCustom", "#######################################################################################")
            Log.e("DxCustom", "# LA VISTA QUE SE ESTÁ INTENTADO AGREGAR YA SE ENCUENTRA VISIBLE EN OTRA VISTA PADRE. #")
            Log.e("DxCustom", "#                       ASEGURATE DE QUE LA VISTA ES HUÉRFANA                         #")
            Log.e("DxCustom", "#######################################################################################")

        }

        return this
    }

    /**
     * Muestra el dialogo y NO lleva un control sobobre los dialogos que se muestran en pantalla.
     *
     * @return Dialog
     */
    fun showDialogReturnDialog(): Dialog{

        try{

            setDataOnDialog()
            animateDialogOnShow()

            dialog.show()


        }catch (e: Exception){

            Log.e(ERROR_TAG, e.printStackTrace().toString())

        }

        return dialog
    }

    /**
     * Muestra el dialog y lleva un control sobre los dialogos que se muestran en pantalla para evitar repeticiones del mismo.
     *
     * @return DxCustom
     */
    fun showDialogReturnDxCustom(): DxCustom{

        try{

            DxCustomSingle.addDxCustom(this) {
                setDataOnDialog()
                animateDialogOnShow()
                dialog.show()
            }

        }catch (e: Exception){

            Log.e(ERROR_TAG, e.printStackTrace().toString())

        }

        return this
    }

    /**
     * * Muestra el dialogo y NO lleva un control sobobre los dialogos que se muestran en pantalla.
     *
     * @return DxCustom
     */
    fun showDialogReturnDxCustomNoControl(): DxCustom{

        try{

            setDataOnDialog()
            animateDialogOnShow()
            dialog.show()


        }catch (e: Exception){

            Log.e(ERROR_TAG, e.printStackTrace().toString())

        }

        return this
    }

    /**
     * Oculta el dialogo con la animacion de cierre.
     */
    fun hideDialog(){

        animateDialogOnHide()

    }

    /**
     * Oculta el loader.
     */
    fun dimissLoader(){
        DxCustomLoader.dimissLoader()
    }

    private fun loadLayoutComponentes(){

        parentDxCustomLayout   =  dialog.findViewById(R.id.parentDxCustomLayout)
        acceptButtonLayout     =  dialog.findViewById(R.id.acceptButton)
        cancelButtonLayout     =  dialog.findViewById(R.id.cancelButton)
        tituloDxCustomLayout   =  dialog.findViewById(R.id.tituloDxCustom)
        mensajeDxCustomLayout  =  dialog.findViewById(R.id.mensajeDxCustom)
        iconoDxCustomLayout    =  dialog.findViewById(R.id.iconDxCustom)
        customViewUpLayout     =  dialog.findViewById(R.id.customViewLinearLayoutDxCustomUp)
        customViewDownLayout   =  dialog.findViewById(R.id.customViewLinearLayoutDxCustomDown)


    }

    /**
     * Pinta los datos proporcionados en el layout proporcionado.
     */
    private fun setDataOnDialog(){


        tituloColor?.let { color ->
            tituloDxCustomLayout?.setTextColor(color)
        }
        tituloSp?.let {
            tituloDxCustomLayout?.textSize= it
        }
        tituloDxCustomLayout?.let {textView ->
            textView.text = tituloDxCustom

        }?:run{

            dialog.window?.findViewById<TextView>(R.id.tituloDxCustomLoading)?.let {
                it.text = tituloDxCustom
            }?:run{

                throwNullPointerException("No se encontro: tituloDxCustomLayout ó tituloDxCustomLoading.")
            }

        }


        mensajeColor?.let { color ->
            mensajeDxCustomLayout?.setTextColor(color)
        }
        mensajeSp?.let {
            mensajeDxCustomLayout?.textSize= it
        }
        mensajeDxCustomLayout?.let {textView ->
            textView.text = Html.fromHtml(mensajeDxCustom, Html.FROM_HTML_MODE_COMPACT)

        }?:run{

            dialog.window?.findViewById<TextView>(R.id.mensajeDxCustomLoading)?.let {
                it.text = Html.fromHtml(mensajeDxCustom, Html.FROM_HTML_MODE_COMPACT)
            }?:run{

                throwNullPointerException("No se encontro: mensajeDxCustomLayout ó mensajeDxCustomLoading.")
            }

        }


        iconoColor?.let { color ->
            iconoDxCustom?.setTint(color)
        }
        iconoDxCustomLayout?.setImageDrawable(iconoDxCustom) ?:run{
            throwNullPointerException("No se encontro: iconoDxCustomLayout.")
        }
    }

    /**
     * Anima el dialogo cuando se muestra
     */
    private fun animateDialogOnShow(){

        when(selectedGravity){
            Gravity.CENTER -> {animateDialogOnShowCenter()}
            Gravity.BOTTOM -> {animateDialogOnShowBottom()}
        }

    }

    /**
     * Anima el dialogo cuando se muestra en el centro
     */
    private fun animateDialogOnShowCenter(){

        if(animarAlSalir){
            parentDxCustomLayout?.let {

                it.scaleY = 0f
                it.scaleX = 0f

            }?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

            parentDxCustomLayout?.visible() ?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

            Handler(Looper.getMainLooper()).postDelayed({

                parentDxCustomLayout?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(DELAY_TIME/3)
                    ?.start()
                    ?:run {
                        throwNullPointerException("No se encontro: parentDxCustomLayout.")
                    }

            }, DELAY_TIME/3)
        }else{
            parentDxCustomLayout?.visible() ?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }
        }
    }

    /**
     * Anima el dialogo cuando se muestra en el bottom
     */
    private fun animateDialogOnShowBottom(){
        if(animarAlSalir){

            parentDxCustomLayout?.let {

                it.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                it.translationY = +1000f + it.measuredHeight

            }?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

            parentDxCustomLayout?.visible() ?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

            Handler(Looper.getMainLooper()).postDelayed({

                parentDxCustomLayout?.animate()?.translationY(0f)?.setDuration(DELAY_TIME)
                    ?.start()
                    ?:run {
                        throwNullPointerException("No se encontro: parentDxCustomLayout.")
                    }

            }, 100)
        }else{
            parentDxCustomLayout?.visible() ?:run {
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }
        }
    }

    /**
     * Anima el dialogo cuando se esconde
     */
    private fun animateDialogOnHide() {

        try{

            DxCustomSingle.removeDxCustom(this){
                if (animarAlEsconder){

                    when(selectedGravity){
                        Gravity.CENTER -> {animateDialogOnHideCenter()}
                        Gravity.BOTTOM -> {animateDialogOnHideBottom()}
                    }

                }else{

                    dialog.dismiss()
                }
            }

        }catch (e: Exception){
            Log.w(ERROR_TAG, "Error al cerrar el dialogo, puede que ya haya sido cerrado por la muerte de una actividad.")
        }

    }

    /**
     * Anima el dialogo cuando se esconde en el centro
     */
    private fun animateDialogOnHideCenter(){
        parentDxCustomLayout?.animate()?.scaleX(0f)?.scaleY(0f)?.setDuration(DELAY_TIME/3)
            ?.start()
            ?:run{
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

        Handler(Looper.getMainLooper()).postDelayed({

            dialog.dismiss()

        }, DELAY_TIME/3)
    }

    /**
     * Anima el dialogo cuando se esconde en el bottom
     */
    private fun animateDialogOnHideBottom(){
        parentDxCustomLayout?.animate()?.translationY(2000f)?.setDuration(DELAY_TIME)
            ?.start()
            ?:run{
                throwNullPointerException("No se encontro: parentDxCustomLayout.")
            }

        Handler(Looper.getMainLooper()).postDelayed({

            dialog.dismiss()

        }, DELAY_TIME)
    }

    /**
     * Verficia los datos.
     */
    private fun verificarExistenciaDeRecursos(){

        try{

            iconoDxCustom = ContextCompat.getDrawable(context!!, R.drawable.dx_default_icon)

        }catch (e: Exception){
            Log.e(ERROR_TAG, e.printStackTrace().toString())
        }

        try {

            layoutDxCustomPersonalizadoBottom = context!!.applicationContext.resources.getLayout(R.layout.dx_custom_default_layout_original)
            layoutDxCustomPersonalizadoCenter = context!!.applicationContext.resources.getLayout(R.layout.dx_custom_default_layout_center_original)

        }catch (e: Exception){
            Log.e(ERROR_TAG, e.printStackTrace().toString())
        }

    }

    /**
     * Función que me devuelve la vista del boton aceptar.
     */
    fun getAcceptButton(): Button? {
        return acceptButtonLayout
    }

    /**
     * Función que me devuelve la vista del boton cancelar.
     */
    fun getCancelButton(): Button? {
        return cancelButtonLayout
    }

    /**
     * Función que lanza IllegalArgumentException con mensaje personalizado.
     */
    @Throws(IllegalArgumentException::class)
    fun throwIllegalArgumentException(mensaje: String = "Error no definido") {
        throw IllegalArgumentException(mensaje)
    }

    /**
     * Función que lanza IllegalStateException con mensaje personalizado.
     */
    @Throws(IllegalStateException::class)
    fun throwIllegalStateException(mensaje: String = "Error no definido") {
        throw IllegalStateException(mensaje)
    }

    /**
     * Función que lanza IllegalStateException con mensaje personalizado.
     */
    @Throws(NullPointerException::class)
    fun throwNullPointerException(mensaje: String = "Error no definido") {
        throw NullPointerException(mensaje)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DxCustom

        if (tituloDxCustom != other.tituloDxCustom) return false
        if (mensajeDxCustom != other.mensajeDxCustom) return false
        if (tituloColor != other.tituloColor) return false
        if (mensajeColor != other.mensajeColor) return false
        if (iconoColor != other.iconoColor) return false
        if (tituloSp != other.tituloSp) return false
        if (mensajeSp != other.mensajeSp) return false
        if (selectedGravity != other.selectedGravity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tituloDxCustom.hashCode()
        result = 31 * result + mensajeDxCustom.hashCode()
        result = 31 * result + (tituloColor ?: 0)
        result = 31 * result + (mensajeColor ?: 0)
        result = 31 * result + (iconoColor ?: 0)
        result = 31 * result + (tituloSp?.hashCode() ?: 0)
        result = 31 * result + (mensajeSp?.hashCode() ?: 0)
        result = 31 * result + selectedGravity
        return result
    }

    override fun toString(): String {

        val gravity = when(selectedGravity){
            17 -> "Gravity.CENTER"
            80 -> "Gravity.BOTTOM"
            else -> "NONE"
        }

        return " \n Titulo='$tituloDxCustom' \n Gravity='$gravity' \n Mensaje='$mensajeDxCustom'"
    }


}

/**
 * Clase para lelvar el control de los DxCustom que se muestran en pantalla.
 */
private object DxCustomSingle{

    /**
     * Variable para guardar los DxCustom que se visualizan.
     */
    private var dxCustomSingleList: MutableList<DxCustom> = mutableListOf()

    /**
     * Metodo que agrega el DxCustom a la lista si no lo contiene, si este DxCustom ya se muestra en pantalla, imprimirá por consola un mensaje.
     *
     * @param dxCustom Instancia de DxCustom a controlar.
     * @param actionOnAdd Accion que se ejecuta al comprobar que no existe, es de uso interno a la libreria.
     */
    fun addDxCustom(dxCustom:DxCustom, actionOnAdd: () -> Unit){

        if(!dxCustomSingleList.contains(dxCustom)){
            dxCustomSingleList.add(dxCustom)
            actionOnAdd.invoke()
        }else{
            Log.e("DxCustom", "####################################################################################")
            Log.e("DxCustom", "# Imposible mostrar un DxCustom, ya se está mostrando uno con información similar. #")
            Log.e("DxCustom", "####################################################################################")
            Log.e("DxCustom", "--------------------------- DATOS DEL DXCUSTOM OMITIDO: ----------------------------")
            Log.e("DxCustom", "$dxCustom")
            Log.e("DxCustom", "------------------------------------------------------------------------------------")

        }

    }

    /**
     * Metodo que elimina el DxCustom a la lista si lo contiene.
     *
     * @param dxCustom Instancia de DxCustom a controlar.
     * @param actionOnRemove Accion que se ejecuta al comprobar que existe, es de uso interno a la libreria.
     */
    fun removeDxCustom(dxCustom: DxCustom, actionOnRemove: () -> Unit){
        if(dxCustomSingleList.contains(dxCustom)){
            dxCustomSingleList.remove(dxCustom)
        }else{
            Log.e("DxCustom", "#################################################")
            Log.e("DxCustom", "# Imposible eliminar un DxCustom no controlado. #")
            Log.e("DxCustom", "#################################################")
        }

        actionOnRemove.invoke()
    }
}

private object DxCustomLoader{

    private var loader: Dialog? = null

    fun displayLoader(dialog: Dialog){

        try{

            loader?.let{
                Log.e("DxCustomLoader", "Ya existe un loader.")
            }?:run{
                dialog.apply {
                    loader = this
                    show()
                }
            }
        }catch (e: Exception){
            Log.e("DxCustomLoader", e.printStackTrace().toString())
        }


    }

    fun dimissLoader(){

        try{
            loader?.let{
                loader?.dismiss()
                loader = null
            }?:run{
                Log.e("DxCustomLoader", "No existe un loader.")
            }
        }catch (e: Exception){
            Log.e("DxCustomLoader", e.printStackTrace().toString())
        }
    }

}