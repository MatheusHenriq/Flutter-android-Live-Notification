class LiveNotificationModel {
  int minutesToDelivery;
  int progress;
  LiveNotificationModel({required this.minutesToDelivery, required this.progress});
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['minutesToDelivery'] = minutesToDelivery;
    data['progress'] = progress;
    return data;
  }
}
