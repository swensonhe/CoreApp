package com.metacto.core.utils.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.metacto.core.domain.CoreConstants
import com.metacto.core.utils.Date
import com.metacto.core.utils.extensions.getAppIconResId
import com.metacto.core.utils.extensions.getLauncherPendingIntent
import com.metacto.coreApp.R
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import kotlin.time.Clock
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.app.Notification as PlatformNotification

class NotificationManager(
    private val context: Context,
    private val notifier: NotifierManager
) : INotificationManager, NotifierManager.Listener {

    private val notificationService = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    private val alarmService = context.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    private var onNewTokenListener: ((String) -> Unit)? = null
    private var onMessageNotificationListener: ((String?, String?) -> Unit)? = null
    private var onDataNotificationListener: ((NotificationPayload) -> Unit)? = null
    private var onNotificationClickedListener: ((NotificationPayload) -> Unit)? = null

    init {
        notifier.addListener(this)
    }

    override fun show(notification: Notification) {
        // Create the notification channel if required
        val channelId = notification.channel?.id ?: INotificationManager.APP_CHANNEL_ID
        val channelName = notification.channel?.name ?: INotificationManager.APP_CHANNEL_NAME
        createNotificationsChannel(
            id = channelId,
            name = channelName
        )

        // Prepare the notification data
        val notificationId = notification.id ?: Clock.System.now().toEpochMilliseconds().toInt()

        val extras = mutableMapOf(
            CoreConstants.KEY_NOTIFICATION_ID to notificationId
        )

        val pendingIntent = notification.pendingIntent ?: context.getLauncherPendingIntent(extras)
        val notificationIcon = notification.icon
            ?: context.getAppIconResId()
            ?: R.drawable.ic_default_notifications_icon

        // Create the notification builder
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setSmallIcon(notificationIcon)
            .setAutoCancel(notification.autoCancel)
            .setPriority(notification.priority)
            .setContentIntent(pendingIntent)
            .setDefaults(notification.defaults)

        // Then show the notification
        notificationService.notify(notificationId, builder.build())
    }

    override fun createNotificationsChannel(
        id: String,
        name: String,
        priority: Int
    ): NotificationChannel? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null
        return when {
            notificationService.getNotificationChannel(id) == null -> {
                val channel = NotificationChannel(id, name, priority).apply {
                    lockscreenVisibility = PlatformNotification.VISIBILITY_PUBLIC
                }
                notificationService.createNotificationChannel(channel)
                channel
            }

            else -> notificationService.getNotificationChannel(id)
        }
    }

    override fun onCreateOrOnNewIntent(intent: Intent?) {
        notifier.onCreateOrOnNewIntent(intent)
    }

    override fun schedule(notification: Notification, date: Date) {
        // Prepare the notification id
        val notificationId = notification.id ?: Clock.System.now().toEpochMilliseconds().toInt()

        // Create the receiver intent
        val intent = Intent(context, NotificationsReceiver::class.java).apply {
            putExtra(NotificationsReceiver.KEY_NOTIFICATION, notification)
        }

        // Create the pending intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Start the alarm
        alarmService.set(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
    }

    override fun scheduleRepeating(
        notification: Notification,
        hourOfDay: Int,
        minute: Int
    ) {
        // Prepare date and repeat interval
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        val repeatInterval = TimeUnit.DAYS.toMillis(1) // Repeated every day

        // Then schedule
        scheduleRepeating(
            notification = notification,
            triggerDate = date,
            repeatIntervalMillis = repeatInterval
        )
    }

    override fun scheduleRepeating(notification: Notification, intervalMinutes: Int) {
        // Prepare date and repeat interval
        val date = Calendar.getInstance().apply {
            add(Calendar.MINUTE, intervalMinutes)
        }
        val repeatIntervalMillis = TimeUnit.MINUTES.toMillis(intervalMinutes.toLong())

        // Then schedule
        scheduleRepeating(
            notification = notification,
            triggerDate = date,
            repeatIntervalMillis = repeatIntervalMillis
        )
    }

    private fun scheduleRepeating(
        notification: Notification,
        triggerDate: Calendar,
        repeatIntervalMillis: Long
    ) {
        // Prepare the notification id
        val notificationId = notification.id ?: Clock.System.now().toEpochMilliseconds().toInt()

        // Create the receiver intent
        val intent = Intent(context, NotificationsReceiver::class.java).apply {
            putExtra(NotificationsReceiver.KEY_NOTIFICATION, notification)
        }

        // Create the pending intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Start the alarm
        alarmService.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerDate.timeInMillis,
            repeatIntervalMillis,
            pendingIntent
        )
    }

    override fun cancelScheduled(id: Int) {
        // Create the receiver intent
        val intent = Intent(context, NotificationsReceiver::class.java)

        // Create the pending intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Then cancel
        alarmService.cancel(pendingIntent)
    }

    override fun removeDelivered(id: Int) {
        notificationService.cancel(id)
    }

    override fun removeAllDelivered() {
        notificationService.cancelAll()
    }

    override fun clearBadgeCount() {
        // In Android to clear badge count you have to clear all notifications in the notification center
        notificationService.cancelAll()
    }

    override suspend fun getPushNotificationToken(): String? {
        return notifier.getPushNotifier().getToken()
    }

    override suspend fun deletePushNotificationToken() {
        notifier.getPushNotifier().deleteMyToken()
    }

    override suspend fun subscribeToTopic(topicName: String) {
        notifier.getPushNotifier().subscribeToTopic(topicName)
    }

    override suspend fun unSubscribeFromTopic(topicName: String) {
        notifier.getPushNotifier().unSubscribeFromTopic(topicName)
    }

    override fun onNewTokenListener(listener: (String) -> Unit) {
        onNewTokenListener = listener
    }

    override fun onReceiveMessageNotification(listener: (title: String?, body: String?) -> Unit) {
        onMessageNotificationListener = listener
    }

    override fun onReceiveDataNotification(listener: (data: NotificationPayload) -> Unit) {
        onDataNotificationListener = listener
    }

    override fun onNotificationClicked(listener: (payload: NotificationPayload) -> Unit) {
        onNotificationClickedListener = listener
    }

    override fun onNewToken(token: String) {
        onNewTokenListener?.invoke(token)
    }

    override fun onPushNotification(title: String?, body: String?) {
        onMessageNotificationListener?.invoke(title, body)
    }

    override fun onPayloadData(data: PayloadData) {
        onDataNotificationListener?.invoke(data)
    }

    override fun onNotificationClicked(data: PayloadData) {
        onNotificationClickedListener?.invoke(data)
    }
}