package by.aurorasoft.kafka.variables;

public interface KafkaVars {

    String SCHEMA_PROP_NAME = "SCHEMA";

    String NOTIFICATION_BACKLOG_TOPIC_NAME = "notification-backlog";
    String NOTIFICATION_BACKLOG_GROUP_ID = "notification-backlog";

    String RECEIVED_COMMANDS_LOG_TOPIC_NAME = "received-commands-log";
    String RECEIVED_COMMANDS_LOG_GROUP_ID = "received-commands-log";

    String COMMANDS_TO_SEND_LOG_TOPIC_NAME = "commands-to-send-log";
    String COMMANDS_TO_SEND_LOG_GROUP_ID = "commands-to-send-log";

    String UNIT_ACTIONS_LOG_TOPIC_NAME = "unit-actions-log";
    String UNIT_ACTIONS_LOG_GROUP_ID = "unit-actions-log";

    String USER_ACTIONS_LOG_TOPIC_NAME = "user-actions-log";
    String USER_ACTIONS_LOG_GROUP_ID = "user-actions-log";

    String RETRANSLATOR_ITEM_ACTION_LOG_TOPIC_NAME = "retranslator-action-log";
    String RETRANSLATOR_ITEM_ACTION_GROUP_ID = "retranslator-action-log";

    String UNIT_RECONNECT_HOOK_TOPIC_NAME = "unit-reconnect-hook-log";
    String UNIT_RECONNECT_HOOK_GROUP_ID = "unit-reconnect-hook-log";

    String TELEGRAM_MESSAGES_TOPIC_NAME = "telegram-messages";
    String TELEGRAM_MESSAGES_GROUP_ID = "telegram-messages";

    String UNIT_LOG_TOPIC_NAME = "unit-log";
    String UNIT_LOG_GROUP_ID = "unit-log";

    String RETRANSLATOR_LOG_TOPIC_NAME = "retranslator-log";
    String RETRANSLATOR_LOG_GROUP_ID = "retranslator-log";

    String MESSAGES_TOPIC_NAME = "messages";
    String MESSAGES_LOG_GROUP_ID = "messages";

    String MESSAGES_MAIN_TOPIC_NAME = "messages-main";
    String MESSAGES_MAIN_LOG_GROUP_ID = "messages-main";

    String MESSAGES_SKO_TOPIC_NAME = "messages-sko";
    String MESSAGES_SKO_LOG_GROUP_ID = "messages-sko";

    String MESSAGES_SKO_TOPIC_NAME_LDQ = "messages-sko-ldq";
    String MESSAGES_SKO_LOG_GROUP_ID_LDQ = "messages-sko-ldq";

    String SMS_MESSAGES_TOPIC_NAME = "sms-messages";
    String SMS_MESSAGES_GROUP_ID = "sms-messages";

    String PERSONAL_TRACKER_PUSH_TOPIC_NAME = "personal-tracker-push";
    String PERSONAL_TRACKER_PUSH_GROUP_ID = "personal-tracker-push";

    String FIX_ORDER_EVENT_TOPIC_NAME = "fix-order-event";
    String FIX_ORDER_EVENT_GROUP_ID = "fix-order-event";
}
