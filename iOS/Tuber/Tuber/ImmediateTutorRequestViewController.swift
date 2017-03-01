//
//  ImmediateTutorRequestViewController.swift
//  Tuber
//
//  Created by Anne on 1/20/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class ImmediateTutorRequestViewController: UIViewController, CLLocationManagerDelegate {

    @IBOutlet weak var currentLocationMap: MKMapView!
    @IBOutlet weak var beginTutoringButton: UIButton!
    
    let defaults = UserDefaults.standard
    
    //URL to our web service
    let makeTutorAvailable = "http://tuber-test.cloudapp.net/ProductRESTService.svc/maketutoravailable"
    
    let manager = CLLocationManager();
    
    var location:CLLocation?
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        location = locations[0]
        
        let span:MKCoordinateSpan = MKCoordinateSpanMake(0.01, 0.01)
        let myLocation:CLLocationCoordinate2D = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
        let region:MKCoordinateRegion = MKCoordinateRegionMake(myLocation, span)
        
        currentLocationMap.setRegion(region, animated: true)
        
        self.currentLocationMap.showsUserLocation = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func beginTutoring(_ sender: Any) {
        
        //created NSURL
        let requestURL = NSURL(string: makeTutorAvailable)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        //getting values from text fields
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let tutorCourse = "CS 4400"//ClassListViewController.selectedClass.className
        let latitude = String(describing: location?.coordinate.latitude)
        let longitude = String(describing: location?.coordinate.longitude)
        
        print(latitude)
        
        //creating the post parameter by concatenating the keys and values from text field
        
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"tutorCourse\":\"" + tutorCourse + "\",\"latitude\":\"" + latitude + "\",\"longitude\":\"" + longitude + "\"}"
        
//        var postParameters = "userEmail=" + userEmail + "&userToken=" + userToken + "&tutorCorse=" + tutorCourse;
//        
//        postParameters += "&latitude=" + String(latitude!) + "&longitude=" + String(longitude!);
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            //parsing the response
            do {
                print(response)
//                //converting resonse to NSDictionary
//                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
//                
//                //parsing the json
//                if let parseJSON = myJSON {
//                    
//                    //creating a string
//                    var msg : String!
//                    
//                    //getting the json response
//                    msg = parseJSON["message"] as! String?
//                    
//                    //printing the response
//                    print(msg)
//                    
//                }
            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
    }
    

}
