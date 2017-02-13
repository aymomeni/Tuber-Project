﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Collections;
using System.Text;

namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IProductRESTService" in both code and config file together.
    [ServiceContract]
    public interface IToDoService
    {
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/createuser",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        MakeUserItem CreateUser(CreateUserItem data);

        /// <summary>
        /// Accepts HTTP request to verify user credentials received match what is in the database 
        /// to allow the user to login to the platform. 
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/verifyuser",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        VerifiedUserItem VerifyUser(UserItem data);

        /// <summary>
        /// Accepts HTTP request to make a tutor available.
        /// </summary>
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/maketutoravailable",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        MakeTutorAvailableResponseItem MakeTutorAvailable(TutorUserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
        UriTemplate = "/deletetutoravailable",
        RequestFormat = WebMessageFormat.Json,
        ResponseFormat = WebMessageFormat.Json,
        BodyStyle = WebMessageBodyStyle.Bare)]
        DeleteTutorResponseItem DeleteTutorAvailable(DeleteTutorUserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/findavailabletutors",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        FindAvailableTutorResponseItem FindAvailableTutors(TutorUserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/pairstudenttutor",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudentTutorPairedItem PairStudentTutor(StudentTutorRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/checkpairedstatus",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        PairedStatusItem CheckPairedStatus(CheckPairedStatusItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/checksessionactivestatusstudent",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        CheckSessionActiveStatusStudentResponseItem CheckSessionActiveStatusStudent(CheckSessionActiveStatusStudentRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/getsessionstatus",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        GetSessionStatusResponseItem GetSessionStatus(GetSessionStatusRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/starttutorsession",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StartTutorSessionResponseItem StartTutorSession(StartTutorSessionItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/endtutorsession",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        EndTutorSessionResponseItem EndTutorSession(EndTutorSessionRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/updatestudentlocation",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        UpdateStudentLocationResponseItem UpdateStudentLocation(UpdateStudentLocationRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/updatetutorlocation",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        UpdateTutorLocationResponseItem UpdateTutorLocation(UpdateTutorLocationRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/ratetutor",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        RateTutorResponseItem RateTutor(RateTutorItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/ratestudent",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        RateStudentResponseItem RateStudent(RateStudentItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/createstudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        CreateStudyHotspotResponseItem CreateStudyHotspot(CreateStudyHotspotRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/findstudyhotspots",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        FindStudyHotspotReturnItem FindStudyHotspots(StudyHotspotItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/joinstudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudyHotspotJoinResponseItem JoinStudyHotspot(StudyHotspotJoinItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/leavestudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudyHotspotLeaveRequestItem LeaveStudyHotspot(StudyHotspotLeaveItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/getstudyhotspotmembers",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudyHotspotResponseItem GetStudyHotspotMembers(StudyHotspotGetMemberItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/deletestudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudyHotspotDeleteResponseItem DeleteStudyHotspot(StudyHotspotDeleteItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/scheduletutor",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        ScheduleTutorResponseItem ScheduleTutor(ScheduleTutorItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/findallscheduletutorrequests",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        FindAllScheduleTutorResponseItem FindAllScheduleTutorRequests(FindAllScheduleTutorRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/acceptstudentscheduledrequest",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        AcceptStudentScheduleRequestResponseItem AcceptStudentScheduledRequest(AcceptStudentScheduleRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/checkscheduledpairedstatus",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        CheckPairedStatusResponseItem CheckScheduledPairedStatus(CheckPairedStatusItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/findallscheduletutoracceptedrequests",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        FindAllScheduleTutorAcceptedResponsetItem FindAllScheduleTutorAcceptedRequests(FindAllScheduleTutorAcceptedRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/startscheduledtutorsession",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StartScheduledTutorSessionResponseItem StartScheduledTutorSession(StartScheduledTutorSessionItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/reporttutorgettutorlist",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        ReportTutorGetTutorListResponseItem ReportTutorGetTutorList(ReportTutorGetTutorListRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/reporttutorgetsessionlist",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        ReportTutorGetSessionListResponseItem ReportTutorGetSessionList(ReportTutorGetSessionListRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/reporttutor",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        ReportTutorResponseItem ReportTutor(ReportTutorRequestItem data);
    }
}
