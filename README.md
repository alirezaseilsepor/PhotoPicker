### implementation 'ir.king-app:photoPicker:1.0.0'

<br>

```kotlin
 val photoPickerDialog = PhotoPickerDialog.Builder()
    .maxSelectSize(3)
    .crop()
    .compress(80)
    .onSelectListener {
        //list photo
    }
    .build()

photoPickerDialog.show(supportFragmentManager, null)
```

<br>
<br>

### Add Permissions

```xml
<root>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
</root>
```

### override Strings

```xml

<root>
    <string name="photo_picker_cancel">Cancel</string>
    <string name="photo_picker_crop">crop</string>
    <string name="photo_picker_ok">OK</string>
    <string name="photo_picker_permission_request_title">Core fundamental are based on these
        permissions
    </string>
    <string name="photo_picker_permission_forward_setting">You need to allow necessary permissions
        in Settings manually
    </string>
    <string name="photo_picker_all_folder">all folder</string>
    <string name="photo_picker_select">Select</string>
    <string name="photo_picker_without_name">without name</string>
</root>
```

<br>
<br>

### override theme

```xml

<root>

    <item name="PhotoPickerCustomStyle">@style/PhotoPickerCustomStyle</item>

    <style name="PhotoPickerCustomStyle" parent="BasePhotoPickerStyle">
        <item name="PhotoPicker_colorPrimary">#FF6E86</item>
        <item name="PhotoPicker_colorPrimaryText">#262626</item>
        <item name="PhotoPicker_colorSecondaryText">#6C6C6C</item>
        <item name="PhotoPicker_colorDisableText">#ACACAC</item>
        <item name="PhotoPicker_colorButtonText">#FFFFFF</item>
        <item name="PhotoPicker_colorDisableButtonText">#ACACAC</item>
        <item name="PhotoPicker_colorDisableButton">#DEDEDE</item>
        <item name="PhotoPicker_colorTintIcon">#FFFFFF</item>
        <item name="PhotoPicker_colorBackgroundPicker">#FFFFFF</item>
        <item name="PhotoPicker_colorBackgroundCrop">#000000</item>
        <item name="PhotoPicker_colorBackgroundItem">#DEDEDE</item>
        <item name="PhotoPicker_colorStrokeItem">#DEDEDE</item>
    </style>
</root>
```
