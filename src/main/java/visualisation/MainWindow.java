package visualisation;

import db.DBRepository;
import db.DbOrmRepository;
import db.models.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class MainWindow {

    private static final Color defaultColor = new Color(151, 172, 209);

    public static void main(String[] args) throws Exception {

        var dbOrm = new DbOrmRepository();

        // Подключаемся к базе данных
        dbOrm.connect();

        try {
            // Получаем студентов из базы данных
            List<Student> studentsFromDB = dbOrm.getStudents();

            // Фильтруем студентов, у которых указан возраст
            List<Student> studentsWithAge = filterStudentsWithValidAge(studentsFromDB);

            // Сколько студентов с указанным возрастом
            System.out.println("Количество студентов с указанным возрастом: " + studentsWithAge.size());

            // Создаем главное окно
            JFrame mainFrame = new JFrame("Анализ студентов по возрасту");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(500, 200);
            mainFrame.setLayout(new BorderLayout());
            mainFrame.getContentPane().setBackground(new Color(240, 240, 240)); // Фон окна

            // Добавляем заголовок
            JLabel titleLabel = new JLabel("Выберите тип графика для отображения", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.DARK_GRAY);
            mainFrame.add(titleLabel, BorderLayout.NORTH);
            titleLabel.setBorder(new EmptyBorder(20, 10, 20, 10));

            // Создаем панель для кнопок
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
            buttonPanel.setBackground(new Color(240, 240, 240));

            // Создаем кнопки
            RoundedButton pieChartButton = new RoundedButton("Круговая диаграмма", defaultColor);
            RoundedButton lineChartButton = new RoundedButton("Линейный график", defaultColor);

            // Устанавливаем одинаковый размер для всех кнопок
            Dimension buttonSize = new Dimension(180, 38); // Ширина = 200, Высота = 40
            pieChartButton.setPreferredSize(buttonSize);
            lineChartButton.setPreferredSize(buttonSize);

            // Добавляем действия для кнопок
            pieChartButton.addActionListener(_ -> {
                // Открываем круговую диаграмму
                PieChartDrawer.drawPieChart(studentsFromDB);
            });

            lineChartButton.addActionListener(e -> {
                // Открываем 4 графика
                LineChartDrawer.drawLineCharts(studentsWithAge);
            });

            // Добавляем кнопки на панель
            buttonPanel.add(pieChartButton);
            buttonPanel.add(lineChartButton);

            // Добавляем панель с кнопками в главное окно
            mainFrame.add(buttonPanel, BorderLayout.CENTER);

            // Устанавливаем окно по центру экрана
            mainFrame.setLocationRelativeTo(null);

            // Делаем окно видимым
            mainFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка: " + e.getMessage());
        } finally {
            // Закрываем соединение с базой данных
            dbOrm.disconnect();
        }
    }

    private static List<Student> filterStudentsWithValidAge(List<Student> students) {
        return students.stream()
                .filter(student -> !student.getAge().isEmpty()
                        && !student.getAge().equals("дата рождения не указана")
                        && !student.getAge().equals("пользователь не найден или не зарегистрирован в вк"))
                .collect(Collectors.toList());
    }
}