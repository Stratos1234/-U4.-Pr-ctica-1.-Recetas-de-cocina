package mx.edu.ittepic.dadm_u4_reseta1;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Principal extends AppCompatActivity {
    EditText identificacion, ingredientes, preparacion, observaciones, nombre;
    ImageButton insertar, consultar, eliminar, actualizar;
    BaseDatos base;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        identificacion = findViewById(R.id.ids);
        observaciones = findViewById(R.id.observacion);
        ingredientes = findViewById(R.id.ingredientes);
        preparacion = findViewById(R.id.preparacion);
        nombre = findViewById(R.id.nombre);

        consultar = findViewById(R.id.editar);
        eliminar = findViewById(R.id.eliminacion);
        actualizar = findViewById(R.id.actualizacion);
        insertar = findViewById(R.id.guardara);
        base = new BaseDatos(this, "primera", null, 1);

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
                num = 0;
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(3);
                num = 0;
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (num == 2) {
                    aplicarActualizar();
                } else {
                    pedirID(2);
                }
            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
                num = 0;
            }
        });
    }

    private void pedirID(final int origen) {
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje = "Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if (origen == 2) {
            mensaje = "Ecriba el id a modificar";
        }
        if (origen == 3) {
            mensaje = "Escriba que desea eliminar";
        }

        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pidoID.getText().toString().isEmpty()) {
                            Toast.makeText(Principal.this, "Debes escribir un numero", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void buscarDato(String idaBuscar, int origen) {
        try {

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM RECETAS WHERE ID=" + idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL, null);
            if (resultado.moveToFirst()) {//mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if (origen == 3) {
                    //se consulto para borrar
                    String dato = idaBuscar + "&" + resultado.getString(1) + "&" + resultado.getString(2) +
                            "&" + resultado.getString(3) + "&" + resultado.getString(4);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                ingredientes.setText(resultado.getString(2));
                preparacion.setText(resultado.getString(3));
                observaciones.setText(resultado.getString(4));
                if (origen == 2) {
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    identificacion.setEnabled(false);

                    AlertDialog.Builder confir = new AlertDialog.Builder(this);
                    confir.setTitle("IMPORTNATE").setMessage("estas seguro que deseas aplicar cambios de actualizacion")
                            .setPositiveButton("si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    num = 2;
                                    Toast.makeText(Principal.this, "Preciona Update de nuevo para confirmar los cambios", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            habilitarBotonesYLimpiarCampos();
                            dialog.cancel();
                        }
                    }).show();
                }
            } else {
                //no hay resultado!
                Toast.makeText(this, "No se ENCONTRO EL RESULTADO", Toast.LENGTH_LONG).show();
            }
            tabla.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se pudo buscar", Toast.LENGTH_LONG).show();
        }

    }

    private void aplicarActualizar() {
        try {
            num = 0;
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "UPDATE RECETAS SET NOMBRE='" + nombre.getText().toString() + "', " +
                    "INGREDIENTES='" + ingredientes.getText().toString() + "'," +
                    "PREPARACION='" + preparacion.getText().toString() + "'," +
                    "OBSERVACIONES='" + ingredientes.getText().toString() +
                    "' WHERE ID=" + identificacion.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "Se actualizo", Toast.LENGTH_LONG).show();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void habilitarBotonesYLimpiarCampos() {
        identificacion.setText("");
        nombre.setText("");
        observaciones.setText("");
        ingredientes.setText("");
        preparacion.setText("");

        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        identificacion.setEnabled(true);
    }

    private void invocarConfirmacionEliminacion(String dato) {
        String datos[] = dato.split("&");
        final String id = datos[0];
        String nombre = datos[1];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("atencion").setMessage("Deseas eliminar la receta: " + nombre)
                .setPositiveButton("Si a todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        eliminarIdtodo(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void eliminarIdtodo(String idEliminar) {
        try {
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM RECETAS WHERE ID=" + idEliminar;
            tabla.execSQL(SQL);
            tabla.close();
            nombre.setText("");
            ingredientes.setText("");
            observaciones.setText("");
            identificacion.setText("");
            preparacion.setText("");

            Toast.makeText(this, "Se elimino el dato", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e) {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_LONG).show();
        }
    }


    private void codigoInsertar() {


        // String mensaje ="Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);


        alerta.setTitle("Atencion")//.setMessage(mensaje)

                .setPositiveButton("Confirmar cambio ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {


                            SQLiteDatabase tabla = base.getWritableDatabase();


                            String SQL = "INSERT INTO RECETAS VALUES(1,'%2','%3','%4','%5')";
                            SQL = SQL.replace("1", identificacion.getText().toString());
                            SQL = SQL.replace("%2", nombre.getText().toString());
                            SQL = SQL.replace("%3", ingredientes.getText().toString());
                            SQL = SQL.replace("%4", preparacion.getText().toString());
                            SQL = SQL.replace("%5", observaciones.getText().toString());
                            tabla.execSQL(SQL);

                            Toast.makeText(Principal.this, "Si se pudo", Toast.LENGTH_LONG).show();
                            tabla.close();
                            nombre.setText("");
                            ingredientes.setText("");
                            observaciones.setText("");
                            identificacion.setText("");
                            preparacion.setText("");

                        } catch (SQLiteException e) {

                            Toast.makeText(Principal.this, "No se pudo", Toast.LENGTH_LONG).show();

                        }
                    }
                })
                .setNegativeButton("Cancelar", null).show();

    }

}
