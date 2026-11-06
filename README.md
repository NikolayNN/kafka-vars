подключить

добавить в pom.xml
```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

   <dependency>
         <groupId>com.github.NikolayNN</groupId>
         <artifactId>kafka-vars</artifactId>
         <version>TAG</version>
   </dependency>
```

Настроить KafkaAdmin используя KafkaAdminFactory

Настроить тему KafkaTopicFactory

### Строковые параметры сообщения

`MessageTransportable.strParams` — `Map<String, String>` для строковых параметров,
например `{"ble_1_id":"0012AB34CD56"}`. Идентификаторы сохраняются целиком,
включая ведущие нули. Числовые параметры по-прежнему передаются в строке `params`.

В новом конструкторе `strParams` передаётся сразу после `params`. Старый конструктор
сохранён и задаёт пустую карту. Отсутствующее или `null` значение при JSON-чтении
также становится пустой картой.

Avro-схему сообщения нужно получать через
`SchemaRepositoryMessageTransportable.getSchemaObject()`. Репозиторий сохраняет порядок
и типы прежних полей и добавляет **в конец** `strParams` типа `map` со значениями
`string` и `default: {}`. Прямой вызов `ReflectData.getSchema(MessageTransportable.class)`
не подходит: он сортирует поля по имени и нарушает совместимость бинарного формата.

`AvroGenericRecordDeserializer` и наследующий его Kafka Streams десериализатор читают
старые и новые сообщения в одной теме. После прежних полей старое сообщение заканчивается,
а новое содержит дополнительную Avro-карту. У старого сообщения reader возвращает
`strParams: {}`. Старая схема зафиксирована в `avro/message-transportable-v1.avsc`.
Default применяется с этой явно заданной схемой записи; сам по себе default
не определяет формат входных байтов.

Прежний reader из библиотеки также читает обычные поля нового сообщения: он завершает
чтение после старых полей и игнорирует добавленную карту. Чтобы использовать строковые
параметры, нужно обновить consumer и его преобразование `GenericRecord`.

Версию `1.0.20-spring-6` использовать для Kafka-сообщений нельзя: в ней поле вставлялось
внутрь прежнего формата. Её немаркированные новые payload несовместимы с описанным
здесь форматом. Исправление рассчитано на переход с сообщений без `strParams`.
