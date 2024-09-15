import 'package:android_live_notification/src/model/live_notification_model.dart';
import 'package:flutter/services.dart';

class LiveNotificationService {
  final MethodChannel _method = const MethodChannel("androidInteractiveNotifications");

  Future<void> startNotifications({required LiveNotificationModel data}) async {
    try {
      await _method.invokeMethod("startNotifications", data.toJson());
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> updateNotifications({required LiveNotificationModel data}) async {
    try {
      await _method.invokeMethod("updateNotifications", data.toJson());
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> finishDeliveryNotification() async {
    try {
      await _method.invokeMethod("finishDeliveryNotification");
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> endNotifications() async {
    try {
      await _method.invokeMethod("endNotifications");
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }
}
