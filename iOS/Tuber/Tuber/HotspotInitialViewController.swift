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
    
    let manager = CLLocationManager();
    
    var location:CLLocation?
    var myLocation:CLLocationCoordinate2D?
    var haveLocation = false
    
    var returnedJSON: [String : AnyObject] = [:]
    var ownerEmail: [String] = []
    var hotspotID: [String] = []
    var longitude: [Double] = []
    var latitude: [Double] = []
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        self.location = locations[0]
        
//        print(String(location!.coordinate.latitude))
//        print(String(location!.coordinate.longitude))
        
        let span:MKCoordinateSpan = MKCoordinateSpanMake(0.1, 0.1)
        self.myLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)

        let region:MKCoordinateRegion = MKCoordinateRegionMake(self.myLocation!, span)
        
        mapview.setRegion(region, animated: true)
        
        self.mapview.showsUserLocation = true
        
        if (!haveLocation)
        {
            findHotspots(String(location!.coordinate.latitude), longitude: String(location!.coordinate.longitude))
            self.haveLocation = true
        }
    }
        
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
        
        self.mapview.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func createNewHotspot(_ sender: Any) {
        
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/createstudyhotspot"
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        //getting values from text fields
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        //        let latitude = String(describing: location?.coordinate.latitude)
        //        let longitude = String(describing: location?.coordinate.longitude)
        
        //creating the post parameter by concatenating the keys and values from text field
//        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"latitude\":\"\(self.location!.coordinate.latitude)\",\"longitude\":\"\(self.location!.coordinate.longitude)\"}"
        
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"topic\": \"Midterm 2\",\"latitude\":\"\(self.location!.coordinate.latitude)\",\"longitude\":\"\(self.location!.coordinate.longitude)\",\"locationDescription\":\"Room 205\"}"
        print(postParameters)
        
        
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            let r = response as? HTTPURLResponse

            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "createHotspot", sender: nil)
                }
            }
            else{
                print(r?.statusCode as Any)
            }
            
        }
        //executing the task
        task.resume()
    }
    
    
    func findHotspots(_ latitude: String, longitude: String)
    {
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/findstudyhotspots"
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        //getting values from text fields
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
//        let latitude = String(describing: location?.coordinate.latitude)
//        let longitude = String(describing: location?.coordinate.longitude)
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"latitude\":\"\(latitude)\",\"longitude\":\"\(longitude)\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["studyHotspots"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                        
                            let aObject = arrJSON[index] as! [String : AnyObject]
                        
                            print(aObject)
                        
                            self.ownerEmail.append(aObject["ownerEmail"] as! String)
                            self.hotspotID.append(aObject["hotspotID"] as! String)
                            self.latitude.append(aObject["latitude"] as! Double)
                            self.longitude.append(aObject["longitude"] as! Double)
                        
                        }
                    }
                }
                print(self.ownerEmail)
                print(self.hotspotID)
                
                OperationQueue.main.addOperation{
                    self.createAnnotations()
                }

            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
    }
    
    // When user taps on the disclosure button you can perform a segue to navigate to another view controller
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        if control == view.rightCalloutAccessoryView{
            
//            print(view.annotation?.title! as Any) // annotation's title
            let hotspotID = view.annotation?.subtitle! as Any
            
            let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/getstudyhotspotmembers"
            
            //created NSURL
            let requestURL = NSURL(string: server)
            
            //creating NSMutableURLRequest
            let request = NSMutableURLRequest(url: requestURL! as URL)
            
            //setting the method to post
            request.httpMethod = "POST"
            
            let defaults = UserDefaults.standard
            
            //getting values from text fields
            let userEmail = defaults.object(forKey: "userEmail") as! String
            let userToken = defaults.object(forKey: "userToken") as! String
            
            //creating the post parameter by concatenating the keys and values from text field
            let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"studyHotspotID\":\"\(hotspotID)\"}"
            
            //adding the parameters to request body
            request.httpBody = postParameters.data(using: String.Encoding.utf8)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            print(postParameters)
            
            //creating a task to send the post request
            let task = URLSession.shared.dataTask(with: request as URLRequest){
                data, response, error in
                
                if error != nil{
                    print("error is \(error)")
                    return;
                }
                
                //parsing the response
                do {
                    //print(response)
                    let members = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                    
                    //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                    if let arrJSON = members["studyHotspots"] {
                        if (arrJSON.count > 0) {
                            for index in 0...arrJSON.count-1 {
                                
                                let aObject = arrJSON[index] as! [String : AnyObject]
                                
                                print(aObject)
                                
                                self.ownerEmail.append(aObject["ownerEmail"] as! String)
                                self.hotspotID.append(aObject["hotspotID"] as! String)
                                self.latitude.append(aObject["latitude"] as! Double)
                                self.longitude.append(aObject["longitude"] as! Double)
                                
                            }
                        }
                    }
                    print(self.ownerEmail)
                    print(self.hotspotID)
                    
                    OperationQueue.main.addOperation{
                        var detailParams: [String] = []
                        
                        detailParams.append((view.annotation?.subtitle!)!)
                        detailParams.append((view.annotation?.title!)!)
                        
                        self.performSegue(withIdentifier: "viewHotspotDetail", sender: detailParams)
                    }
                    
                } catch {
                    print(error)
                }
                
            }
            //executing the task
            task.resume()
            
        }
    }
    
//    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
//        var view = mapView.dequeueReusableAnnotationView(withIdentifier: "AnnotationView Id")
//        if view == nil{
//            view = MKPinAnnotationView(annotation: annotation, reuseIdentifier: "AnnotationView Id")
//            view!.canShowCallout = true
//        } else {
//            view!.annotation = annotation
//        }
//        
//        view?.leftCalloutAccessoryView = nil
//        view?.rightCalloutAccessoryView = UIButton(type: UIButtonType.detailDisclosure)
//        
//        return view
//    }

    // Here we add disclosure button inside annotation window
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        
        print("viewForannotation")
        if annotation is MKUserLocation {
            //return nil
            return nil
        }
        
        let reuseId = "pin"
        var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseId) as? MKPinAnnotationView
        
        if pinView == nil {
            //println("Pinview was nil")
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            pinView!.canShowCallout = true
            pinView!.animatesDrop = true
        }
        
        let button = UIButton(type: UIButtonType.detailDisclosure) as UIButton // button with info sign in it
        
        pinView?.rightCalloutAccessoryView = button
        
        
        return pinView
    }
    
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
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "viewHotspotDetail"
        {
            let appointmentInfo = sender as! [String]
            print(appointmentInfo[0])
            print(appointmentInfo[1])
            
            if let destination = segue.destination as? HotspotDetailViewController
            {
                destination.hotspotID = appointmentInfo[0]
                destination.memberList = appointmentInfo[1]
            }
        }
    }

}
