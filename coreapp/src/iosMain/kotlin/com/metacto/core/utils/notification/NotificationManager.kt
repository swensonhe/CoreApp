package com.metacto.core.utils.notification

import com.metacto.core.domain.CoreConstants
import com.metacto.core.utils.Date
import com.metacto.core.utils.toDateComponents
import com.mmk.kmpnotifier.extensions.onApplicationDidReceiveRemoteNotification
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import kotlin.time.Clock
import platform.Foundation.NSDateComponents
import platform.UIKit.UIApplication
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class NotificationManager(
    private val notificationCenter: UNUserNotificationCenter,
    private val notifier: NotifierManager
) : INotificationManager, NotifierManager.Listener {

    private var onNewTokenListener: ((String) -> Unit)? = null
    private var onMessageNotificationListener: ((String?, String?) -> Unit)? = null
    private var onDataNotificationListener: ((NotificationPayload) -> Unit)? = null
    private var onNotificationClickedListener: ((NotificationPayload) -> Unit)? = null

    init {
        notifier.addListener(this)
    }

    override fun onApplicationDidReceiveRemoteNotification(userInfo: Map<Any?, *>) {
        notifier.onApplicationDidReceiveRemoteNotification(userInfo)
    }

    override fun schedule(notification: Notification, date: Date) {
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = date.toDateComponents(),
            repeats = false
        )

        show(
            notification = notification,
            trigger = trigger
        )
    }

    override fun scheduleRepeating(notification: Notification, hourOfDay: Int, minute: Int) {
        val dateComponents = NSDateComponents().apply {
            setHour(hourOfDay.toLong())
            setMinute(minute.toLong())
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = true
        )

        show(
            notification = notification,
            trigger = trigger
        )
    }

    override fun scheduleRepeating(notification: Notification, intervalMinutes: Int) {
        val timeInterval = intervalMinutes * 60.0
        show(
            notification = notification,
            trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                timeInterval = timeInterval,
                repeats = true
            )
        )
    }

    private fun show(notification: Notification, trigger: UNNotificationTrigger) {
        val notificationId = notification.id ?: Clock.System.now().toEpochMilliseconds().toInt()

        val notificationContent = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setSound(UNNotificationSound.defaultSound)
            notification.body?.let {
                setBody(it)
            }

            setUserInfo(mapOf(CoreConstants.KEY_NOTIFICATION_ID to notificationId.toString()))
        }
        val notificationRequest = UNNotificationRequest.requestWithIdentifier(
            identifier = notificationId.toString(),
            content = notificationContent,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(notificationRequest) { error ->
            error?.let { println("Error showing notification: $error") }
        }
    }

    override fun cancelScheduled(id: Int) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(id.toString())
        )
    }

    override fun removeDelivered(id: Int) {
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(
            listOf(id.toString())
        )
    }

    override fun removeAllDelivered() {
        notificationCenter.removeAllDeliveredNotifications()
    }

    override fun clearBadgeCount() {
        UIApplication.sharedApplication.applicationIconBadgeNumber = 0
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