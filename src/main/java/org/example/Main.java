package org.example;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) {
        try {
            // Создаём временную HTML-страницу
            Path tempHtml = Files.createTempFile("speech_recognition_", ".html");
            tempHtml.toFile().deleteOnExit();

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Распознавание речи</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        text-align: center;
                        padding: 40px;
                        background: #f5f9ff;
                    }
                    button {
                        padding: 14px 28px;
                        font-size: 20px;
                        background: #2196F3;
                        color: white;
                        border: none;
                        border-radius: 8px;
                        cursor: pointer;
                        margin: 20px 0;
                    }
                    button:hover {
                        background: #1976D2;
                    }
                    #result {
                        margin-top: 30px;
                        padding: 20px;
                        background: white;
                        border-radius: 10px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                        min-height: 30px;
                        font-size: 20px;
                        color: #222;
                    }
                </style>
            </head>
            <body>
                <h1>🎙️ Распознавание речи</h1>
                <p>Нажмите кнопку и говорите на русском языке</p>
                <button onclick="startSpeech()">Распознать речь</button>
                <div id="result">Результат появится здесь</div>

                <script>
                    function startSpeech() {
                        const resultDiv = document.getElementById('result');
                        resultDiv.innerText = 'Слушаю...';

                        if (!('webkitSpeechRecognition' in window)) {
                            resultDiv.innerText = '❌ Web Speech API не поддерживается.\\nПопробуйте открыть в Chrome или Edge.';
                            return;
                        }

                        const recognition = new webkitSpeechRecognition();
                        recognition.lang = 'ru-RU';
                        recognition.interimResults = false;
                        recognition.maxAlternatives = 1;

                        recognition.onresult = (event) => {
                            const transcript = event.results[0][0].transcript;
                            resultDiv.innerHTML = '✅ Вы сказали:<br><strong>' + transcript + '</strong>';
                        };

                        recognition.onerror = (event) => {
                            console.error('Ошибка:', event.error);
                            resultDiv.innerText = '❌ Ошибка: ' + event.error;
                        };

                        recognition.onend = () => {
                            // Можно автоматически начать снова, если нужно
                        };

                        recognition.start();
                    }
                </script>
            </body>
            </html>
            """;

            // Записываем HTML в файл
            Files.writeString(tempHtml, htmlContent, StandardOpenOption.CREATE);

            // Открываем в браузере
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(tempHtml.toUri());
                System.out.println("✅ Открыто в браузере: " + tempHtml.toUri());
            } else {
                System.err.println("❌ Невозможно открыть браузер. Откройте вручную: " + tempHtml.toAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}