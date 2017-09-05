# LogSearcher

Logsearcher
=====================

Программа для поиска заданного текста в лог файлах. Возможность выбора папки на жестком диске, 
в которой будет проиходить поиск заданного текста, включая все вложенные папки.

Реализована возможность ввода текста поиска и ввода типа расширения файлов, 
в которых будет осуществляться поиск (расширение по умолчанию *.log).

Результаты поиска выводятся в левой части приложения в виде дерева файловой системы только те файлы, 
в которых был обнаружен заданный текст.

В правой части приложения выводится содержимое файла с возможностью навигации по найденному тексту (вперед/назад).

Реализован быстрый поиск и навигация в больших файлах (более 1Г). А также возможность открывать файлы в новых табах.