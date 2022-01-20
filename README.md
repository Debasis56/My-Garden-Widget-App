# My Garden

## The app
My Garden is a simple game that allows you to add plants to your garden and keep them alive by watering them on time.
The app illustrates the power of widgets and collection widgets by making it easier for the user to monitor and water their plants from the home screen.
It makes use of a <b>widget provider</b> which defines the customized widget screen and actions. We use the widget as a <b>Broadcast Receiver</b> to provide services for watering plants that haven't been watered. The plants with the longest last_Watered time is shown in order to be watered first. Once the plant exceeds the maximum_watered time, the plant dies, after which it could no longer be watered. The widget screen also changes from the simple linear layout to grid layout once the width of the widget exceeds 300dp. In the grid layout all the plants in the garden are shown. Each plant is associated with a fillInIntent which tends to store the id of each plant and open that respective plant when clicked. The Grid Layout automatically refreshes when it notices any changes with the plant in garden. This is a dynamic app and the app keeps updating the image of the plants as it gets on growing older.

## Screenshots


![Screenshot 2022-01-20 at 7 37 58 PM](https://user-images.githubusercontent.com/56356721/150354666-0cc519e8-6b82-448d-a4b7-9779ecacea31.png)
![Screenshot 2022-01-20 at 7 40 22 PM](https://user-images.githubusercontent.com/56356721/150354712-1c3afee2-a687-4110-b3e9-8b7f77c7055f.png)
![Screenshot 2022-01-20 at 7 39 05 PM](https://user-images.githubusercontent.com/56356721/150354724-9fb7aefa-93cb-459e-832e-52dc216103b0.png)
![Screenshot 2022-01-20 at 7 38 26 PM](https://user-images.githubusercontent.com/56356721/150354746-3d4f79ee-0a98-47c6-ba4b-58b9ded4ce57.png)
![Screenshot 2022-01-20 at 7 36 52 PM](https://user-images.githubusercontent.com/56356721/150354762-9360df23-3706-4cc8-8ef1-672127675655.png)
