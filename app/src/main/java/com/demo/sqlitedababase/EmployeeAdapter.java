package com.demo.sqlitedababase;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;


public class EmployeeAdapter extends ArrayAdapter<Employee> {

    Context mCtx;
    int listLayoutRes;
    List<Employee> employeeList;
    SQLiteDatabase mDatabase;

    public EmployeeAdapter(Context mCtx, int listLayoutRes, List<Employee> employeeList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, employeeList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.employeeList = employeeList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Employee employee = employeeList.get(position);


        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        TextView textViewContact = view.findViewById(R.id.textViewContact);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);


        textViewName.setText(employee.getName());
        textViewEmail.setText(employee.getEmail());
        textViewContact.setText(String.valueOf(employee.getContact()));
        textViewAddress.setText(employee.getAddress());


        ImageView buttonDelete = view.findViewById(R.id.buttonDeleteEmployee);
        ImageView buttonEdit = view.findViewById(R.id.buttonEditEmployee);

        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(employee);
            }
        });

        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM Employee WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{employee.getId()});
                        reloadEmployeesFromDatabase();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void updateEmployee(final Employee employee) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_employee, null);
        builder.setView(view);


        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editEmail = view.findViewById(R.id.editEmail);
        final EditText editContact = view.findViewById(R.id.editContact);
        final EditText editAddress = view.findViewById(R.id.editAddress);

        editTextName.setText(employee.getName());
        editEmail.setText(employee.getEmail());
        editContact.setText(employee.getContact());
        editAddress.setText(employee.getAddress());


        final AlertDialog dialog = builder.create();
        dialog.show();

        // CREATE METHOD FOR EDIT THE FORM
        view.findViewById(R.id.buttonUpdateEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String contact = editContact.getText().toString().trim();
                String address = editAddress.getText().toString().trim();

                if (name.isEmpty()) {
                    editTextName.setError("Name can't be blank");
                    editTextName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editEmail.setError("Email can't be blank");
                    editEmail.requestFocus();
                    return;
                }

                String sql = "UPDATE Employee \n" +
                        "SET Name = ?, \n" +
                        "Email = ?,\n"+
                        "Contact = ?,\n"+
                        "Address= ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, email,contact,address, String.valueOf(employee.getId())});
                Toast.makeText(mCtx, "Employee Updated", Toast.LENGTH_SHORT).show();
                reloadEmployeesFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private void reloadEmployeesFromDatabase() {
        Cursor cursorEmployees = mDatabase.rawQuery("SELECT * FROM Employee", null);
        if (cursorEmployees.moveToFirst()) {
            employeeList.clear();
            do {
                employeeList.add(new Employee(
                        cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getString(4)
                ));
            } while (cursorEmployees.moveToNext());
        }
        cursorEmployees.close();
        notifyDataSetChanged();
    }

}
