//
//  ListTutorViewController.swift
//  Tuber
//
//  Created by Hyunjin Cho on 2017. 4. 6..
//  Copyright © 2017년 Tuber. All rights reserved.
//

import UIKit
import CoreLocation

class ListTutorViewController: UIViewController, CLLocationManagerDelegate
{
    var tutorFirstNames: [String] = [];
    var tutorLastNames: [String] = [];
    var tutorEmails: [String] = [];
    
    let manager = CLLocationManager();
    
    var location:CLLocation?;
    var myLocation:CLLocationCoordinate2D?;
    var haveLocation = false;
    
    var returnedJSON: [String : AnyObject] = [:];
    var longitude: [Double] = [];
    var latitude: [Double] = [];
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation])
    {
        self.location = locations[0]
        self.myLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
    }
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
        //prepTutorList();
    }
    override func viewDidAppear(_ animated: Bool)
    {}
    override func didReceiveMemoryWarning()
    {super.didReceiveMemoryWarning()}
    
    func prepTutorList()
    {
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/findavailabletutors";
        
        let requestURL = URL(string: server)
        
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
       // let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\"tutorCourse\":\"  \(course)\"latitude\":\"  \(self.location!.coordinate.latitude)\"longitude\":\(self.location!.coordinate.longitude)\"}";
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"latitude\":\"\(self.location!.coordinate.latitude)\",\"longitude\":\"\(self.location!.coordinate.longitude)\"}"
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
        let task = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            
            if error != nil
            {
                print("error is \(error)")
                return;
            }
            
            let r = response as? HTTPURLResponse
            print(r?.statusCode)
            
            do
            {
                let tutors = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = tutors["tutorList"]
                {
                    if (arrJSON.count > 0)
                    {
                        for index in 0...arrJSON.count-1
                        {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            print(aObject)
                            
                            var name = aObject["tutorFirstName"] as! String
                            name += " "
                            name += aObject["tutorLastName"] as! String
                            
                            self.tutorFirstNames.append(aObject["tutorFirstName"] as! String)
                            self.tutorLastNames.append(aObject["tutorLastName"] as! String)
                            self.tutorEmails.append(aObject["tutorEmail"] as! String)
                            
                        }
                    }
                }
            }
            catch
            {
                print(error)
            }
        }
        //executing the task
        task.resume()
    }
}
