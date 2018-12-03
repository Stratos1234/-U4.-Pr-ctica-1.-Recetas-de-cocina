package mx.edu.ittepic.dadm_u4_reseta1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper { //super clase, interfaz de dbms pero se hace manual

    public BaseDatos( Context context,  String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //crear
        //Se ejeucuta cunado la aplicacion(Ejemplo ejercicio 1)se ejecuta en el CEL
        //Sirve para construir en el SQLite que esta  en el CEL las tablas que la APP requiere para funcionar
        db.execSQL("CREATE TABLE RECETAS(ID INTEGER PRIMARY KEY,NOMBRE VARCHAR(200),INGREDIENTES VARCHAR(1000),PREPARACION VARCHAR(1000),OBSERVACIONES VARCHAR(500))"); //realizada todo excepto select, funcuina insert,create_table,delete,update


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//actualizar,cuando se modifica la estructra de la tabla
        //las versiones, control de  version se empieza en 1
        //se ejecuta cuando el oncreate crea las tablas, toda la alteracion es el oncreate(actualizacion)
        //update actualizacion menor y upgrate es una actualizacion mas grande

    }
}





