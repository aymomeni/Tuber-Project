//
//  HotspotInitialViewController.swift
//  Tuber
//
//  Created by Anne on 2/2/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class HotspotInitialViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate {
    
    @IBOutlet weak var mapview: MKMapView!
    @IBOutlet weak var createHotspotButton: UIButton!
    
    // Variables for location
    let manager = CLLocationManager();
    var location:CLLocation?
    var myLocation:CLLocationCoordinate2D?
    var haveLocation = false
    
    // Hotspot Data
//    var returnedJSON: [String : AnyObject] = [:]
    var ownerEmail: [String] = []
    var hotspotID: [String] = []
    var longitude: [Double] = []
    var latitude: [Double] = []
    
    /**
     * This function gets the current location of the user and shows it on the mapview.
     */
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        self.location = locations[0]
        
        if (!haveLocation)
        {
            let span:MKCoordinateSpan = MKCoordinateSpanMake(0.1, 0.1)
            self.myLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
            
            let region:MKCoordinateRegion = MKCoordinateRegionMake(self.myLocation!, span)
            
            mapview.setRegion(region, animated: true)
            findHotspots(String(location!.coordinate.latitude), longitude: String(location!.coordinate.longitude))
            self.haveLocation = true
        }
        
        self.mapview.showsUserLocation = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
        
        self.mapview.delegate = self
        
        self.title = "Study Hotspot"
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        
        self.navigationController?.navigationBar.isTranslucent = false
        
        self.createHotspotButton.backgroundColor = UIColor.darkGray
        self.createHotspotButton.layer.cornerRadius = 5
        createHotspotButton.layer.borderWidth = 1
        
        var navArray:Array = (self.navigationController?.viewControllers)!
        if(navArray[navArray.count - 2] is ActiveHotspotViewController)
        {
            navArray.remove(at: navArray.count - 2)
            
            self.navigationController?.viewControllers = navArray
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
    * This function takes the user to the hotspot creation form.
    */
    @IBAction func createNewHotspot(_ sender: Any) {
        
        let latitude = "\(self.location!.coordinate.latitude)"
        let longitude = "\(self.location!.coordinate.longitude)"
        
        var toSend = [String]()
        toSend.append(latitude)
        toSend.append(longitude)
        
        performSegue(withIdentifier: "createHotspot", sender: toSend)
        
    }
    
    /**
     * This calls the database to find all nearby hotspots.
     */
    func findHotspots(_ latitude: String, longitude: String)
    {
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/findstudyhotspots"
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"latitude\":\"\(latitude)\",\"longitude\":\"\(longitude)\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            // Parsing the response
            do {
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                if let arrJSON = hotspots["studyHotspots"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            self.ownerEmail.append(aObject ["topic"] as! String)
                            self.hotspotID.append(aObject["hotspotID"] as! String)
                            self.latitude.append(aObject["latitude"] as! Double)
                            self.longitude.append(aObject["longitude"] as! Double)
                            
                        }
                    }
                }
                
                // Create the annotations for each hotspot based on it's data
                OperationQueue.main.addOperation{
                    self.createAnnotations()
                }
                
            } catch {
                print(error)
            }
            
        }
        // Executing the task
        task.resume()
    }
    
    /**
     * This calls the database to find the members of the selected hotspot when the info button is pressed on the hotspot anotaion.
     */
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        if control == view.rightCalloutAccessoryView{
            
            let hotspotID = (view.annotation?.subtitle!)! as String
            
            // Set up the post request
            let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/getstudyhotspotmembers"
            let requestURL = NSURL(string: server)
            let request = NSMutableURLRequest(url: requestURL! as URL)
            request.httpMethod = "POST"
            
            // Create the post parameters
            let defaults = UserDefaults.standard
            let userEmail = defaults.object(forKey: "userEmail") as! String
            let userToken = defaults.object(forKey: "userToken") as! String
            let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"hotspotID\":\"\(hotspotID)\"}"
            
            // Adding the parameters to request body
            request.httpBody = postParameters.data(using: String.Encoding.utf8)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            // Creating a task to send the post request
            let task = URLSession.shared.dataTask(with: request as URLRequest){
                data, response, error in
                
                if error != nil{
                    print("error is \(error)")
                    return;
                }
                
                var hotspotMembers: [String] = []
                
                // Parsing the response
                do {
                    let members = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                    
                    if let arrJSON = members["hotspotMembers"] {
                        if (arrJSON.count > 0) {
                            for index in 0...arrJSON.count-1 {
                                
                                let aObject = arrJSON[index] as! [String : AnyObject]
                                
                                print(aObject)
                                let firstName = aObject["firstName"] as! String
                                let lastName = aObject["lastName"] as! String
                                
                                hotspotMembers.append(firstName + " " + lastName)
                            }
                        }
                    }
                    
                    // Put all the member names in a list format.
                    var memberList = String()
                    if (hotspotMembers.count > 1)
                    {
                        for n in 0...hotspotMembers.count - 2
                        {
                            memberList.append(hotspotMembers[n])
                            memberList.append(", ")
                        }
                        memberList.append(hotspotMembers[hotspotMembers.count - 1])
                    }
                    else if (hotspotMembers.count == 1)
                    {
                        memberList.append(hotspotMembers[0])
                    }
                    
                    // Set up the sender for the segue
                    OperationQueue.main.addOperation{
                        var detailParams: [String] = []
                        
                        detailParams.append((view.annotation?.subtitle!)!)
                        detailParams.append(memberList)
                        
                        self.performSegue(withIdentifier: "viewHotspotDetail", sender: detailParams)
                    }
                    
                } catch {
                    print(error)
                }
                
            }
            // Executing the task
            task.resume()
            
        }
    }
    
    /**
     * This method adds the info button on the hotspot pins
     */
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        
        if annotation is MKUserLocation {
            return nil
        }
        
        let reuseId = "pin"
        var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseId) as? MKPinAnnotationView
        
        if pinView == nil {
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            pinView!.canShowCallout = true
            pinView!.animatesDrop = true
        }
        
        let button = UIButton(type: UIButtonType.detailDisclosure) as UIButton // button with info sign in it
        
        pinView?.rightCalloutAccessoryView = button
        
        return pinView
    }
    
    /**
     * This method creates the annotations after the data is received from the database
     */
    func createAnnotations()
    {
        var annotations = [MKPointAnnotation]()
        
        if (ownerEmail.count > 0){
            for index in 0...ownerEmail.count-1 {
                
                let annotation1 = MKPointAnnotation()
                annotation1.coordinate = CLLocationCoordinate2DMake(latitude[index], longitude[index])
                annotation1.title = ownerEmail[index]
                annotation1.subtitle = hotspotID[index]
                annotations.append(annotation1)
            }
        }
        
        mapview.addAnnotations(annotations)
    }
    
    /**
     * This calls the database to find all nearby hotspots.
     */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "viewHotspotDetail"
        {
            let appointmentInfo = sender as! [String]
            
            if let destination = segue.destination as? HotspotDetailViewController
            {
                destination.hotspotID = appointmentInfo[0]
                destination.memberList = appointmentInfo[1]
            }
        }
        if segue.identifier == "createHotspot"
        {
            let coordinates = sender as! [String]
            
            if let destination = segue.destination as? CreateHotspotViewController
            {
                destination.latitude = coordinates[0]
                destination.longitude = coordinates[1]
            }
        }
        
    }
    
}
