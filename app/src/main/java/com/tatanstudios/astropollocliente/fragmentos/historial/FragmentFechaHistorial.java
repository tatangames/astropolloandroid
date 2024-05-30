package com.tatanstudios.astropollocliente.fragmentos.historial;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.tatanstudios.astropollocliente.R;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class FragmentFechaHistorial extends Fragment {

    Button btnFecha1;
    Button btnFecha2;
    Button btnBuscar;
    TextView txtFecha1;
    TextView txtFecha2;

    TextView txtTitulo;

    String fecha1 = "";
    String fecha2 = "";

    private static final String CERO = "0";
    private static final String BARRA = "-";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    // elegir fecha para ver historial
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_historial_fecha, container, false);


        txtTitulo = vista.findViewById(R.id.txtToolbar);
        txtFecha2 = vista.findViewById(R.id.txtFecha2);
        txtFecha1 = vista.findViewById(R.id.txtFecha1);
        btnBuscar = vista.findViewById(R.id.btnBuscar);
        btnFecha2 = vista.findViewById(R.id.btnFecha2);
        btnFecha1 = vista.findViewById(R.id.btnFecha1);


        txtTitulo.setText(getString(R.string.historial));

        btnFecha1.setOnClickListener(v -> elegirFecha1());
        btnFecha2.setOnClickListener(v -> elegirFecha2());
        btnBuscar.setOnClickListener(v -> buscarPorFecha());

        return vista;
    }

    void elegirFecha1(){
        //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
        DatePickerDialog recogerFecha = new DatePickerDialog(getActivity(),  (view, year, month, dayOfMonth) -> {

            //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
            final int mesActual = month + 1;
            //Formateo el día obtenido: antepone el 0 si son menores de 10
            String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
            //Formateo el mes obtenido: antepone el 0 si son menores de 10
            String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
            //Muestro la fecha con el formato deseado
            txtFecha1.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
            txtFecha1.setVisibility(View.VISIBLE);
            fecha1 = year + BARRA + mesFormateado + BARRA + diaFormateado;
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    void elegirFecha2(){
        //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
        DatePickerDialog recogerFecha = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
            final int mesActual = month + 1;
            //Formateo el día obtenido: antepone el 0 si son menores de 10
            String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
            //Formateo el mes obtenido: antepone el 0 si son menores de 10
            String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
            //Muestro la fecha con el formato deseado
            txtFecha2.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
            txtFecha2.setVisibility(View.VISIBLE);
            fecha2 = year + BARRA + mesFormateado + BARRA + diaFormateado;
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    void buscarPorFecha(){

        if(TextUtils.isEmpty(fecha1)){
            Toasty.info(getActivity(), "Elegir fecha DE").show();
            return;
        }

        if(TextUtils.isEmpty(fecha2)){
            Toasty.info(getActivity(), "Elegir fecha HASTA").show();
            return;
        }

        FragmentBuscarHistorial fragmentInfo = new FragmentBuscarHistorial();

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment.getClass().equals(fragmentInfo.getClass())) return;

        Bundle bundle = new Bundle();
        bundle.putString("KEY_FECHA1", String.valueOf(fecha1));
        bundle.putString("KEY_FECHA2", String.valueOf(fecha2));
        fragmentInfo.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentInfo)
                .addToBackStack(null)
                .commit();
    }


}
