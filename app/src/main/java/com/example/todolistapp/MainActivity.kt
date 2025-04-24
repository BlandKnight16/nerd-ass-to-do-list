package com.example.todolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    //Salvar a lista de tarefas no SharedPreferences
    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("todo_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("task_list", tasks.toSet())
        editor.apply()
    }

    //Carregar a lista de tarefas salvas
    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("todo_prefs", MODE_PRIVATE)
        val savedTasks = sharedPreferences.getStringSet("task_list", emptySet())
        tasks.clear()
        tasks.addAll(savedTasks ?: emptySet())
    }

    //Declaração de variáveis para os componetes da UI
    private lateinit var taskListView: ListView
    private lateinit var taskInput: EditText
    private lateinit var addTaskButton: Button
    private lateinit var tasks: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var addSoundList: List<Int>
    private lateinit var completeSoundList: List<Int>

    private lateinit var popupImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //liga este código ao layout

        popupImage = findViewById(R.id.popupImage)

        //Encontar os copmponetes da UI pelos IDs
        taskInput = findViewById(R.id.taskInput) // Caixa de input
        addTaskButton = findViewById(R.id.addTaskButton) //Botão de adicionar tarefa
        taskListView = findViewById(R.id.taskListView) //Mostra a lista de registrados

        //Cria a list para guardar afazeres
        tasks = ArrayList()

        adapter = object : ArrayAdapter<String>(this, R.layout.list_item, tasks) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.list_item, parent, false)
                val taskText = view.findViewById<TextView>(R.id.taskText)
                taskText.text = getItem(position)
                return view
            }
        }

        taskListView.adapter = adapter
        /*Cria an adaptador para conectar a lista de afazeres para ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        taskListView.adapter = adapter*/

        //Carrega as tarefas salvas
        loadTasks()
        adapter.notifyDataSetChanged()

        //Carrega o audio da pasta raw
        //lista de tarefa adicionada
        addSoundList = listOf(
            R.raw.add_sound_1,
            R.raw.add_sound_2,
            R.raw.add_sound_3
        )

        //lista de tarefa completa
        completeSoundList = listOf(
            R.raw.complete_sound,
            R.raw.complete_sound_2,
            R.raw.complete_sound_3
        )

        //Quando o botão "addtask" é clicado...
        addTaskButton.setOnClickListener {
            val task = taskInput.text.toString() //Get text from input box
            if (task.isNotEmpty()) {
                tasks.add(task)
                adapter.notifyDataSetChanged()
                saveTasks() //salva as tarefas
                taskInput.text.clear()

                playRandomSound(addSoundList)
                showPopup(R.drawable.add_popup)
                }
            }
            taskListView.setOnItemClickListener { _, _, position, _ ->
                tasks.removeAt(position)
                adapter.notifyDataSetChanged()
                saveTasks()

                playRandomSound(completeSoundList)
                showPopup(R.drawable.complete_popup)
            }
        }

    private fun playRandomSound(soundList: List<Int>) {
        val randomSound = soundList[Random.nextInt(soundList.size)]
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(this, randomSound)
        mediaPlayer.start()
    }
    private fun showPopup(imageResId: Int) {
            popupImage.animate().cancel()
            popupImage.clearAnimation()
            popupImage.visibility = ImageView.GONE

            popupImage.setImageResource(imageResId)
            popupImage.alpha = 0f
            popupImage.visibility = ImageView.VISIBLE

            popupImage.animate().alpha(1f).setDuration(250).withEndAction {
                popupImage.animate().alpha(0f).setDuration(500).setStartDelay(500).withEndAction {
                    popupImage.visibility = ImageView.GONE
                    popupImage.animate().cancel()
                }
            }
    }

    //When the app is closed, release the MediaPlayer to free memory
    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}